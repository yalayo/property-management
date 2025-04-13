(ns app.server.core
	(:require [com.stuartsierra.component :as component]
            [com.brunobonacci.mulog :as mu]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [io.pedestal.interceptor :refer [interceptor]]
            [jdbc-ring-session.core :as jdbc-ring-session]))

(def csp-interceptor
  (interceptor
   {:name ::content-security-policy
    :leave (fn [context]
             (assoc-in context [:response :headers "Content-Security-Policy"]
                       "default-src 'self'; script-src 'self' https://cdn.jsdelivr.net; object-src 'none'; base-uri 'self';"))}))

(defrecord ServerComponent [config datasource routes]
  component/Lifecycle
  
  (start [component]
         (let [session-interceptor (middlewares/session {:store (jdbc-ring-session/jdbc-store (datasource) {:table :session_store})})
               server (-> {:env :prod
                           ::http/routes (route/expand-routes (get-in routes [:routes (:active-route config)]))
                           ::http/resource-path "/public"
                           ::http/type :immutant
                           ::http/host "0.0.0.0"
                           ::http/port (:port config)
                           ::http/secure-headers nil}
                          (http/default-interceptors)
                          (update ::http/interceptors concat [session-interceptor csp-interceptor])
                          (http/create-server)
                          (http/start))]
           (mu/log :server-started :message (str "Starting server with " (name (:active-route config)) " routes!"))
           (assoc component :server server)))
  
  (stop [component]
        (when-let [server (:server component)]
          (http/stop server))
        (mu/log :server-stopped :message "Stopping web server!")
        (assoc component :server nil)))

(defn server-component
  [config]
  (map->ServerComponent {:config config}))