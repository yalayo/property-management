(ns app.storage.core
  (:require [com.stuartsierra.component :as component]))

(defrecord StorageComponent [config]
  component/Lifecycle

  (start [component])

  (stop [component]))

(defn storage-component [config]
  (map->StorageComponent config))
