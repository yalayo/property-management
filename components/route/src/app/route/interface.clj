(ns app.route.interface
  (:require [integrant.core :as ig]
            [app.route.core :as core]))

(defn route-component [config]
  (core/route-component config))

(defmethod ig/init-key ::external-routes [_ {:keys [user-routes html-routes]}]
  (into #{} (concat user-routes html-routes)))

(defmethod ig/init-key ::internal-routes [_ {:keys [routes]}]
  (println "External Routes: " routes)
  routes)