(ns app.storage.datahike
  (:require [datahike.api :as d]
            [datahike-jdbc.core]
            [com.brunobonacci.mulog :as mu])
  (:import (clojure.lang ExceptionInfo)))

(defn get-config [dabase-name]
  {:store {:backend :jdbc
           :dbtype "postgresql"
           :host (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_HOST") "localhost")
           :port 5432
           :user "user"
           :password (System/getenv "DB_PASSWORD")
           :table dabase-name
           :dbname "property-management"}})

(defn get-connection [config]
  (try
    (when-not (d/database-exists? config)
      (d/create-database config))
    (d/connect config)
    (catch ExceptionInfo e
      (mu/log :log-exception :exception e))))

(defn transact [data database-name]
  (let [config (get-config database-name)
        connection (get-connection config)]
    (d/transact connection data)))

(defn query [query database-name]
  (let [config (get-config database-name)
        connection (get-connection config)]
    (d/q {:query query}
         connection)))