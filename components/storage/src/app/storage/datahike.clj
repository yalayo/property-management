(ns app.storage.datahike
  (:require [datahike.api :as d]
            [datahike-jdbc.core]
            [com.brunobonacci.mulog :as mu])
  (:import (clojure.lang ExceptionInfo)))

(defn get-config [database-name]
  (let [environment (System/getenv "ENVIRONMENT")]
    (case environment
      "prod" {:store {:backend :jdbc
                      :dbtype "postgresql"
                      :host (System/getenv "DB_HOST")
                      :port 5432
                      :user "user"
                      :password (System/getenv "DB_PASSWORD")
                      :table database-name
                      :dbname "property-management"}}
      "local" #_{:store {:backend :mem :id database-name}} {:store {:backend :file :path (str "tmp/" database-name)}}
      {:store {:backend :jdbc
               :dbtype "postgresql"
               :host "localhost"
               :port 5432
               :user (System/getenv "DB_USER")
               :password (System/getenv "DB_PASSWORD")
               :table database-name
               :dbname "property-management"}})))

(defonce db-connections (atom {}))

(defn get-connection-old [database-name]
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

(defn get-connection [database-name]
  (let [config (get-config database-name)]
    (try
      (when-not (d/database-exists? config)
        (d/create-database config))
      (d/connect config)
      (catch ExceptionInfo e
        (mu/log :log-exception :exception e)))))

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
      (d/transact conn schema-to-transact)
      (d/release conn))))

(defn transact [data database-name]
  (try
    (let [conn (get-connection database-name)]
      ;(transact-schema )
      (d/transact conn data)
      (d/release conn))
    (catch clojure.lang.ExceptionInfo e
      (println "EXCEPTION: " e)
      (throw e))))

(defn query [query database-name]
  (try
    (let [conn (get-connection database-name)]
      (d/q {:query query}
           (d/db conn))) 
    (catch java.sql.SQLException e
      (println "EXCEPTION: " e)
      (throw e))))

(defn query-with-parameter [query database-name value]
  (try
    (let [conn (get-connection database-name)]
      (d/q {:query query} (d/db conn) value))
    (catch java.sql.SQLException e
      (println "EXCEPTION: " e)
      (throw e))))

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
    (d/transact conn schema-to-transact))
  )