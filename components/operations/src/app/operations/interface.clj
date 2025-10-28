(ns app.operations.interface
  (:require [integrant.core :as ig]
            [app.operations.core :as core]
            [app.operations.routes :as routes]
            [app.operations.persistance :as persistance]))

(defn operations-component [config]
  (core/operations-component config))

(defn get-routes []
  routes/routes)

(defmethod ig/init-key ::storage
  [_ {:keys [database-name]}]
  (persistance/init database-name))

(defmethod ig/halt-key! ::storage
  [_ conn]
  (persistance/stop conn))

(defmethod ig/init-key ::routes [_ _]
  (routes/get-routes))