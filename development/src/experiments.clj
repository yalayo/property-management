(ns experiments
  (:require [app.web.interface :as web]))

(def server (web/start))

(comment
  
  (web/stop server)
  )