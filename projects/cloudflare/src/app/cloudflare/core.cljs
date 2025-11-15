(ns app.cloudflare.core
  (:require [integrant.core :as ig]
            [app.worker.core :as worker]))

(def config
  {::worker/handler {}})

(defonce system (atom nil))

(defn start []
  (reset! system (ig/init config)))

(defn stop []
  (when @system
    (ig/halt! @system)
    (reset! system nil)))

(defn restart []
  (stop)
  (start))

(defn init []
  (start))

(def handler (::worker/handler @system))