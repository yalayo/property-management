(ns app.web.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [jdbc-ring-session.core :as jdbc-ring-session]
            [app.html.interface :as html]
            #_[app.user.interface :refer [get-routes get-datasource]]))

#_(def session-interceptor
  (middlewares/session {:store (jdbc-ring-session/jdbc-store (get-datasource) {:table :session_store})}))

(def service
  (-> {:env :prod
       ::http/routes (route/expand-routes (into #{} (html/get-routes) #_(concat (get-routes) (html/get-routes))))
       ::http/resource-path "/public"
       ::http/type :immutant
       ::http/host "0.0.0.0"
       ::http/port 8080}
      (http/default-interceptors)
      (update ::http/interceptors concat [#_session-interceptor])
      http/create-server))

(defn start []
  (http/start service))

(defn stop [server]
  (http/stop server))