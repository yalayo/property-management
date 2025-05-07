(ns app.operations.interface
  (:require [app.operations.core :as core]
            [app.operations.routes :as routes]))

(defn operations-component [config]
  (core/operations-component config))

(defn get-routes []
  routes/routes)