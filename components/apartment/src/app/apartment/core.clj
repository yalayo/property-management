(ns app.apartment.core
  (:require [com.stuartsierra.component :as component]
            [app.apartment.persistance :as persistance]))

(defrecord ApartmentComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn apartment-component [config]
  (map->ApartmentComponent config))