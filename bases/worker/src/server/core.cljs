(ns server.core
  (:require
   [integrant.core :as ig]
   [server.router]
   [server.handler]))

(def config
  {:server/router {}
   :server/handler {:router (ig/ref :server/router)}})

(defonce system (ig/init config))

;; Export to Workers runtime
(set! (.-default (.-exports js/module))
      #js {:fetch (:server/handler system)})