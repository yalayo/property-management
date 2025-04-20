(ns app.apartment.interface
  (:require [app.apartment.core :as core]
            [app.apartment.routes :as routes]))

(defn apartment-component [config]
  (core/apartment-component config))

(defn get-routes []
  routes/routes)