(ns app.property.interface
  (:require [app.property.core :as core]))

(defn property-component [config]
  (core/property-component config))

(defn get-routes []
  core/routes)

(defn get-internal-routes []
  core/internal-routes)