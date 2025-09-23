(ns app.user.interface
  (:require [integrant.core :as ig]
            [app.user.core :as core]
            [app.user.routes :as routes]
            [app.user.persistance :as persistance]
            [app.user.database :as db]))

(defn user-component [config]
  (core/user-component config))

(defn get-routes []
  routes/routes)

(defn get-schema []
  persistance/schema)

(defn get-internal-routes []
  routes/internal-routes)

(defn get-datasource []
  db/ds)

(defn wrap-jwt-auth [handler]
  (core/wrap-jwt-auth handler))

(defmethod ig/init-key ::routes [_ {:keys [shell core]}]
  (routes/get-routes shell core))