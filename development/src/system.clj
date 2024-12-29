(ns system
  (:require [com.stuartsierra.component :as component]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]))

(def config {:db-spec {:dbtype "postgres"
                    :host (if (= (System/getenv "ENVIRONMENT") "prod") "prod-db" "localhost")
                    :dbname "property-management"
                    :username "user"
                    :password (if (= (System/getenv "ENVIRONMENT") "prod") "hrdata@2024" "volley@2024")
                    :dataSourceProperties {:socketTimeout 30}}})

(defn create-system [config]
  (component/system-map
   :datasource (storage/datasource-component config)
   :route (route/route-component config)
   :server (component/using
            (server/server-component config)
            [:datasource :route])))

(defn start []
  (let [system (-> config
                   (create-system)
                   (component/start-system))]
    (println "Starting system for project: main with config")
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))

(comment
  (start)
  )