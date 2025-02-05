(ns app.storage.interface
  (:require [app.storage.core :as core]
            [app.storage.datahike :as datahike]))

(defn datasource-component [config]
  (core/datasource-component config))

(defn transact [data database-name]
  (datahike/transact data database-name))

(defn query [query database-name]
  (datahike/query query database-name))