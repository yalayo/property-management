(ns app.route.interface
  (:require [integrant.core :as ig]
            [app.route.core :as core]
            [app.apartment.interface :as apartment]))

(defn route-component [config]
  (core/route-component config))

(defmethod ig/init-key ::external-routes [_ {:keys [user-routes html-routes]}]
  (into #{} (concat user-routes html-routes)))

(defmethod ig/init-key ::internal-routes 
  [_ {:keys [operations-routes properties-routes tenants-routes apartments-routes accounts-routes]}]
  (into #{} (concat operations-routes properties-routes tenants-routes apartments-routes accounts-routes)))