(ns app.storage.interface
  (:require [integrant.core :as ig]
            [app.storage.core :as core]
            [app.storage.datahike :as datahike]))

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

(defmethod ig/init-key ::pool
  [_ _]
  (datahike/create-pool))

(defmethod ig/halt-key! :db/pool
  [_ pool]
  (datahike/close-pool pool))

(defmethod ig/init-key ::storage
  [_ {:keys [database-name pool schema]}]
  (datahike/init database-name pool schema))

(defmethod ig/halt-key! ::storage
  [_ conn]
  (datahike/stop conn))

(defmethod ig/init-key ::operations
  [_ {:keys [database-name pool schema]}]
  (datahike/init database-name pool schema))

(defmethod ig/halt-key! ::operations
  [_ conn]
  (datahike/stop conn))