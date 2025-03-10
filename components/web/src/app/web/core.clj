(ns app.web.core
  (:require [io.pedestal.http :as http]
            [hiccup2.core :as h]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [jdbc-ring-session.core :as jdbc-ring-session]
            [app.html.interface :as html]
            [ring.util.response :as response]
            [app.user.interface :refer [get-routes get-datasource]]))

(def session-interceptor
  (middlewares/session {:store (jdbc-ring-session/jdbc-store (get-datasource) {:table :session_store})}))

(def service
  (-> {:env :prod
       ::http/routes (route/expand-routes (into #{} (concat (get-routes) (html/get-routes))))
       ::http/resource-path "/public"
       ::http/type :immutant
       ::http/host "0.0.0.0"
       ::http/port 8080}
      (http/default-interceptors)
      (update ::http/interceptors concat [session-interceptor])
      http/create-server))

(defn start []
  (http/start service))

(defn stop [server]
  (http/stop server))