(ns app.worker.core
  (:require [integrant.core :as ig]
            ["cloudflare:workers" :refer [DurableObject]]
            [reitit.core :as r]
         	  [clojure.string :as s]
            [app.worker.async :refer [js-await]]
            [app.worker.durable-objects :as do]
            [app.worker.cf :as cf :refer [defclass]]))

;; usage example of Durable Objects as a short-lived state
;; for user presence tracking in multiplayer web app
(defclass ^{:extends DurableObject} PresenceDurableObject [ctx env]
  Object
  (constructor [this ctx env]
               (super ctx env))

  (add-user-presence+ [this id timestamp]
                      (js-await [_ (do/storage-put+ ctx id timestamp)
                                 users (do/storage-list+ ctx)
                                 now (js/Date.now)]
                                (doseq [[id _] (->> (cf/js->clj users)
                                                    (filter (fn [[id ts]] (> (- now ts) 10000))))]
                                  (do/storage-delete+ ctx id))
                                (do/storage-list+ ctx))))

(def base-routes
  ["/api"])

(defn handle-route [route request env ctx]
  (let [method (keyword (s/lower-case (.-method ^js request)))
        handler (get-in route [:data method])]
    (if handler
      (handler route request env ctx)
      (cf/response-error {:error "Not implemented"}))))

(defn init [{:keys [upload-routes]}]
  (let [routes (into base-routes upload-routes)
        router (r/router routes)
        handler #js {:fetch (cf/with-handler router handle-route)}]
    handler))

(defmethod integrant.core/init-key ::handler
  [_ routes]
  (init routes))