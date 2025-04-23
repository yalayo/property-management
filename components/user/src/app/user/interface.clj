(ns app.user.interface
  (:require [app.user.core :as core]
            [app.user.routes :as routes]
            [app.user.database :as db]))

(defn user-component [config]
  (core/user-component config))

(defn get-routes []
  routes/routes)

(defn get-internal-routes []
  routes/internal-routes)

(defn get-datasource []
  db/ds)

(defn wrap-jwt-auth [handler]
  (core/wrap-jwt-auth handler))