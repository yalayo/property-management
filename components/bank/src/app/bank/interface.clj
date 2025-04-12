(ns app.bank.interface
  (:require [app.bank.core :as core]
            [app.bank.persistance :as persistance]))

(defn get-routes []
  core/routes)

(defn get-internal-routes []
  core/internal-routes)

(defn list-accounts []
  (persistance/list-accounts))