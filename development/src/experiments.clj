(ns experiments
  (:require
   ;[app.web.interface :as web]
   [clojure.string :as str]))

;(def server (web/start))

(comment
  
  ;(web/stop server)
  )

;;Create an Enviroment variable with the following format:
;;PM_DB=property-management;localhost;user;1234
(defn get-db-login []
  (let [db-env (System/getenv "PM_DB")]
    (when db-env
      (let [[db-name db-host db-user db-password] (str/split db-env #";")]
        {:dbname db-name
         :host db-host
         :username db-user
         :password db-password}))))

(comment

  (let [dbdata (get-db-login)]
    (println dbdata))
  
  )