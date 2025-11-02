(ns app.tenant.interface
  (:require [integrant.core :as ig]
            [app.tenant.core :as core]
            [app.tenant.routes :as routes]))

(defn tenant-component [config]
  (core/tenant-component config))

(defn get-routes []
  routes/routes)

(defmethod ig/init-key ::routes [_ {:keys [storage]}]
  (routes/get-routes storage))