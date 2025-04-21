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

(defonce db-connections (atom {}))

(defn get-connection [database-name]
  (let [config (get-config database-name)]
    (if-let [existing-conn (@db-connections database-name)]
      existing-conn
      (try
        (when-not (d/database-exists? config)
          (d/create-database config))
        (let [conn (d/connect config)]
          (swap! db-connections assoc database-name conn)
          conn)
        (catch ExceptionInfo e
          (mu/log :log-exception :exception e))))))

(defn reset-connection! [database-name]
  (when-let [existing-conn (@db-connections database-name)]
    (try
      ;; Close the old connection if possible
      (d/release existing-conn)
      (catch Exception e
        (mu/log :log-exception :exception e :msg "Error while releasing the closed connection"))))
  ;; Remove the old connection from the atom
  (swap! db-connections dissoc database-name)
  ;; Force a new connection to be created
  (get-connection database-name))

(defn transact-schema [schema database-name]
  (let [conn (get-connection database-name)
        existing-idents (into #{} (map first (d/q '[:find ?ident
                                    :where [?e :db/ident ?ident]]
                                  (d/db conn))))
        schema-to-transact (filter #(not (contains? existing-idents (:db/ident %))) schema)]
    (println "Schema to transact: " schema-to-transact)
    (when (seq schema-to-transact)
      (d/transact conn schema-to-transact))))

(defn transact [data database-name]
  (try
    (d/transact (get-connection database-name) data)
    (catch clojure.lang.ExceptionInfo e
      (if (re-find #"has been closed\(\)" (ex-message e))
        (do
          (println "Connection was closed, resetting...")
          (reset-connection! database-name)
          (d/transact (get-connection database-name) data))
        (throw e)))))

(defn query [query database-name]
  (try 
    (d/q {:query query}
       (d/db (get-connection database-name)))
    (catch java.sql.SQLException e
      (when (.contains (.getMessage e) "has been closed()")
        (println "Connection was closed, resetting...")
        (reset-connection! database-name)
        (d/q {:query query}
             (d/db (get-connection database-name)))
        (throw e)))))

(comment
  "Experiment to transact only new schema"
  (d/q '[:find ?ident
         :where [?e :db/ident ?ident]]
       (d/db (get-connection "properties")))

  (def new-schema
    [{:db/ident :name
      :db/valueType :db.type/string
      :db/cardinality :db.cardinality/one}

     {:db/ident :user/age
      :db/valueType :db.type/long
      :db/cardinality :db.cardinality/one}])

  (def existing-idents
    (into #{} (map first (d/q '[:find ?ident
                                :where [?e :db/ident ?ident]]
                              (d/db (get-connection "properties"))))))

  (def schema-to-transact
    (filter #(not (contains? existing-idents (:db/ident %))) new-schema))

  (seq schema-to-transact)

  (when (seq schema-to-transact)
    (d/transact conn schema-to-transact)))