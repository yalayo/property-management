(ns app.storage.interface
  (:require [app.storage.core :as core]
            [app.storage.datahike :as datahike]))

(defn datasource-component [config]
  (core/datasource-component config))

(defn transact-schema [data database-name]
  (datahike/transact-schema data database-name))

(defn transact [data database-name]
  (datahike/transact data database-name))

(defn query [query database-name]
  (datahike/query query database-name))