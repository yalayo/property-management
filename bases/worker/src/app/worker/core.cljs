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

(def allowed-origins
  #{"http://localhost:8081"
    "https://anudis.com"
    "https://www.anudis.com"})

(defn cors-headers-for [origin]
  (if (allowed-origins origin)
    {"Access-Control-Allow-Origin" origin
     "Vary" "Origin"
     "Access-Control-Allow-Methods" "GET, POST, PUT, PATCH, DELETE, OPTIONS"
     "Access-Control-Allow-Headers" "Content-Type, Authorization"
     "Access-Control-Max-Age" "86400"}
    ;; origin not allowed
    {"Access-Control-Allow-Origin" "null"
     "Vary" "Origin"}))

(defn ensure-js-response [resp]
  (if (instance? js/Response resp)
    resp
    (let [{:keys [status headers body]} resp
          js-headers (if (map? headers)
                       (clj->js headers)
                       #js {})]
      (js/Response. (or body "") #js {:status (or status 200)
                                      :headers js-headers}))))

(defn add-cors-response [resp origin] 
  (let [response (ensure-js-response resp)
        hdrs (.-headers response)]
    (doseq [[k v] (cors-headers-for origin)]
      (.set hdrs k v))
    response))

(def base-routes
  ["/api"])

(defn handle-route [route request env ctx]
  (let [origin (.get (.-headers request) "Origin")
        method (.-method request)]
    (if (= method "OPTIONS") ;; Preflight
      (add-cors-response (cf/response nil {:status 204}) origin)
      (let [method-k (keyword (.toLowerCase method))
            handler   (get-in route [:data method-k])]
        (if (some? handler)
          ;; IMPORTANT: wait for the handler to finish!
          (js-await [resp (handler route request env ctx)]
                    (add-cors-response resp origin))
          (add-cors-response (cf/response-error {:error "Not implemented"}) origin))))))

(defn init [{:keys [upload-routes]}]
  (let [routes (into base-routes upload-routes)
        router (r/router routes)
        handler #js {:fetch (cf/with-handler router handle-route)}]
    handler))

(defmethod integrant.core/init-key ::handler
  [_ routes]
  (init routes))