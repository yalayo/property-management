(ns app.tenant.core
  (:require [com.stuartsierra.component :as component]
            [app.tenant.persistance :as persistance]))

(defrecord TenantComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn tenant-component [config]
  (map->TenantComponent config))