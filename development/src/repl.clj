(ns repl
  (:require [nrepl.server :as nrepl])
  (:gen-class))

(defonce server (atom nil))

(defn start-repl []
  (reset! server (nrepl/start-server :bind "0.0.0.0" :port 7000))
  (println "nREPL server started on port 7000"))

(defn stop-repl []
  (when @server
    (nrepl/stop-server @server)
    (reset! server nil)
    (println "nREPL server stopped")))

(defn -main [& args]
  (start-repl)
  ;; start your app logic here
  (println "App started"))