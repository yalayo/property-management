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

(defn store-operation [data]
  (let [kind (:kind data)]
    (case kind
      :expense (persistance/store-expense data)
      :start (persistance/store-ocupancy data)
      :end (persistance/store-ocupancy data))))