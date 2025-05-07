(ns app.operations.core
  (:require [com.stuartsierra.component :as component]
            [app.operations.persistance :as persistance]))

(defrecord OperationsComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn operations-component [config]
  (map->OperationsComponent config))