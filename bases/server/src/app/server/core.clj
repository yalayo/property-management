(ns app.server.core
	(:require [com.stuartsierra.component :as component]
            [integrant.core :as ig]
            [com.brunobonacci.mulog :as mu]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :refer [interceptor]]))

(defn cors-response [origin]
  {"Access-Control-Allow-Origin" origin
   "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
   "Access-Control-Allow-Headers" "Content-Type, Authorization"
   "Access-Control-Allow-Credentials" "true"})

(defn allowed-origin? [origin]
  (boolean (re-matches #"https:\/\/([a-z0-9-]+\.)?busqandote\.com" origin)))

(def cors-interceptor
  {:name ::cors
   :leave (fn [context]
            (let [origin (get-in context [:request :headers "origin"])]
              (if (and origin (allowed-origin? origin))
                (update context :response #(merge % (cors-response origin)))
                context)))})

(def csp-interceptor
  (interceptor
   {:name ::content-security-policy
    :leave (fn [context]
             (assoc-in context [:response :headers "Content-Security-Policy"]
                       "default-src 'self'; script-src 'self' https://cdn.jsdelivr.net; object-src 'none'; base-uri 'self';"))}))

(defrecord ServerComponent [config routes]
  component/Lifecycle
  
  (start [component]
         (let [server (-> {:env :prod
                           ::http/routes (route/expand-routes (get-in routes [:routes (:active-route config)]))
                           ::http/resource-path "/public"
                           ::http/type :immutant
                           ::http/host "0.0.0.0"
                           ::http/port (:port config)
                           ::http/secure-headers nil}
                          (http/default-interceptors)
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

(defn start-server [port active-route routes]
  (let [server (-> {:env :prod
                    ::http/routes (route/expand-routes routes)
                    ::http/resource-path "/public"
                    ::http/type :immutant
                    ::http/host "0.0.0.0"
                    ::http/port port
                    ::http/secure-headers nil}
                   (http/default-interceptors)
                   (http/create-server)
                   (http/start))]
    (mu/log :server-started :message (str "Starting server with " (name active-route) " routes!"))
    server))

;; Implementing integrant
(defmethod ig/init-key ::server [_ {:keys [port active-route routes]}]
  (println "External")
  (start-server port active-route routes))

(defmethod ig/init-key ::internal-server [_ {:keys [port active-route routes]}]
  (println "Internal")
  (start-server port active-route routes))

(defmethod ig/halt-key! ::server [_ server]
  (when server
    (http/stop server)
    (mu/log :server-stopped :message "Stopping external server!")))

(defmethod ig/halt-key! ::internal-server [_ server]
  (when server
    (http/stop server)
    (mu/log :server-stopped :message "Stopping internal server!")))