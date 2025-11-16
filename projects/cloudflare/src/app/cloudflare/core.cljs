(ns app.cloudflare.core
  (:require [integrant.core :as ig]
            [app.worker.core :as worker]
            [app.excel.interface :as excel]))

(def config
  {::excel/routes {} 
   ::worker/handler {:excel-routes (ig/ref ::excel/routes)}})

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
  (start)
  (::worker/handler @system))

(def handler (init))