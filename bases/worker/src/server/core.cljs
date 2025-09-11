(ns server.core
  (:require ["cloudflare:workers" :refer [DurableObject]]
            [reitit.core :as r]
         	  [clojure.string :as s]
            [lib.async :refer [js-await]]
            [server.cf.durable-objects :as do]
            [server.cf :as cf :refer [defclass]]
            [app.user.interface :as user]))

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

(defn todos-get [route request env ctx]
  (cf/response-edn {:result :hello} {:status 200}))

(def base-routes
  ["/api"])

(def routes
    (into base-routes (user/get-routes) #_(concat (excel/routes) (user/routes))))

(def router
  (r/router routes))

(defn handle-route [route request env ctx]
  (let [method (keyword (s/lower-case (.-method ^js request)))
        handler (get-in route [:data method])]
    (if handler
      (handler route request env ctx)
      (cf/response-error {:error "Not implemented"}))))

;; entry point
(def handler
  #js {:fetch (cf/with-handler router handle-route)})