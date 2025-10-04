(ns app.html.interface
  (:require [integrant.core :as ig]
            [app.html.core :as core]))

(defn get-routes []
  core/routes)

(defn respond [content title]
  (core/respond content title))

(defn respond-with-params [content value title]
  (core/respond-with-params content value title))

(defmethod ig/init-key ::routes [_ {:keys [core controller]}]
  core/routes)