(ns app.storage.interface
  (:require [integrant.core :as ig]
            [app.storage.core :as core]
            [app.storage.datahike :as datahike]))

(defmethod ig/init-key ::storage
  [_ {:keys [database-name schema]}]
  (datahike/init database-name schema))

(defmethod ig/halt-key! ::storage
  [_ conn]
  (datahike/stop conn))

(defn datasource-component [config]
  (core/storage-component config))

(defn transact-schema [data database-name]
  (datahike/transact-schema data database-name))

(defn transact [data database-name]
  (datahike/transact data database-name))

(defn query [query database-name]
  (datahike/query query database-name))

(defn query-with-parameter [query database-name value]
  (datahike/query-with-parameter query database-name value))