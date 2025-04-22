(ns app.account.core
  (:require [com.stuartsierra.component :as component]
            [app.account.persistance :as persistance]))

(defrecord AccountComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn account-component [config]
  (map->AccountComponent config))