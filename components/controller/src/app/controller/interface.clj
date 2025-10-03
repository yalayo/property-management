(ns app.controller.interface
  (:require
   [integrant.core :as ig]
   [app.controller.core :as core]))

(defmethod ig/init-key ::controller [_ {:keys [storage]}]
  (core/init storage))