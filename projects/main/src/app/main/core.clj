(ns app.main.core
  (:require [com.stuartsierra.component :as component]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]))

(defn create-system [config]
  (component/system-map
   :datasource (storage/datasource-component config)
   :route (route/route-component config)
   :server (component/using
            (server/server-component config)
            [:datasource :route])))

(defn -main []
  (let [system (-> {}
                   (create-system)
                   (component/start-system))]
    (println "Starting system for project: main with config")
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))