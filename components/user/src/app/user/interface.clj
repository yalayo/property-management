(ns app.user.interface
  (:require [app.user.core :as core]
            [app.user.routes :as routes]
            [app.user.database :as db]))

(defn get-routes []
  routes/routes)

(defn get-internal-routes []
  routes/internal-routes)

(defn get-datasource []
  db/ds)