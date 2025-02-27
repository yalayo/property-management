(ns app.property.interface
  (:require [app.property.core :as core]))

(defn get-routes []
  core/routes)

(defn get-internal-routes []
  core/internal-routes)