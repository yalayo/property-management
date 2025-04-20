(ns app.tenant.interface
  (:require [app.tenant.core :as core]
            [app.tenant.routes :as routes]))

(defn tenant-component [config]
  (core/tenant-component config))

(defn get-routes []
  routes/routes)