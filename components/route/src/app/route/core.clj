(ns app.route.core
  (:require [com.stuartsierra.component :as component]))

(defrecord RouteComponent [config]
  component/Lifecycle

  (start [component]
    (println "Starting route-component")
    (assoc component :routes config))

  (stop [component]
    (println "Stopping server-component")
    (assoc component :routes nil)))

(defn route-component [config]
  (map->RouteComponent config))