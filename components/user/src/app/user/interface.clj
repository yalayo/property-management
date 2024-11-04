(ns app.user.interface
  (:require [app.user.core :as core]
            [app.user.database :as db]))

(defn get-routes []
  core/routes)

(defn get-datasource []
  db/ds)