(ns app.account.interface
  (:require [app.account.core :as core]
            [app.account.routes :as routes]))

(defn account-component [config]
  (core/account-component config))

(defn get-routes []
  routes/routes)