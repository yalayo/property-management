(ns app.property.interface
  (:require [app.property.core :as core]
            [app.property.routes :as routes]))

(defn property-component [config]
  (core/property-component config))

(defn get-routes []
  routes/routes)

(defn get-internal-routes []
  core/internal-routes)