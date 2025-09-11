(ns server.core
  (:require [reitit.core :as r]
            [clojure.string :as s]
            [server.cf :as cf]
            [app.user.interface :as user]))

(def base-routes
  ["/api"])

#_(def routes
  (into base-routes (user/get-routes) #_(concat (excel/routes) (user/routes))))

(defn todos-get [route request env ctx]
  (cf/response-edn {:result :hello} {:status 200}))


;; Define routes correctly with nested vectors
(def routes
  ["/api" ["/todos" {:name ::todos :get  todos-get}]])

(def router
  (r/router [routes])) ;; important: wrap in a vector of top-level routes

(defn handle-route [route request env ctx]
  (let [method (keyword (s/lower-case (.-method ^js request)))
        handler (get-in route [:data method])]
    (if handler
      (handler route request env ctx)
      (cf/response-error {:error "Not implemented"}))))

;; entry point
(def handler
  #js {:fetch (cf/with-handler router handle-route)})