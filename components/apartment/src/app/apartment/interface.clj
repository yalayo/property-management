(ns app.apartment.interface
  (:require [integrant.core :as ig]
            [app.apartment.core :as core]
            [app.apartment.routes :as routes]))

(defn apartment-component [config]
  (core/apartment-component config))

(defn get-routes []
  routes/routes)

(defmethod ig/init-key ::routes [_ {:keys [storage]}]
  (routes/get-routes storage))