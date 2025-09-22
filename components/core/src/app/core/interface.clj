(ns app.core.interface
  (:require
   [integrant.core :as ig]
   [app.core.system :as core]))

;; Initialize the state atom
(defmethod ig/init-key ::domain [_ {:keys [initial]}]
  (core/init initial))

;; Clean shutdown (nothing to stop, but good to define)
(defmethod ig/halt-key! ::domain [_ {:keys [state]}]
  (core/stop state))