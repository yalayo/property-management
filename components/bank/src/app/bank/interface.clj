(ns app.bank.interface
  (:require [app.bank.core :as core]))

(defn get-routes []
  core/routes)

(defn get-internal-routes []
  core/internal-routes)