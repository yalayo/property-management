(ns app.account.interface
  (:require [integrant.core :as ig]
            [app.account.core :as core]
            [app.account.routes :as routes]))

(defn account-component [config]
  (core/account-component config))

(defn get-routes []
  routes/routes)

(defmethod ig/init-key ::routes [_ {:keys [storage]}]
  (routes/get-routes storage))