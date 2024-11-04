(ns app.web.interface
  (:require [app.web.core :as core]))

(defn start []
  (core/start))

(defn stop [server]
  (core/stop server))