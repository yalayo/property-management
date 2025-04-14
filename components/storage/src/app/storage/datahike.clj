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

(defn transact-schema [schema database-name]
  (let [conn (get-connection database-name)
        existing-idents (into #{} (map first (d/q '[:find ?ident
                                    :where [?e :db/ident ?ident]]
                                  (d/db conn))))
        schema-to-transact (filter #(not (contains? existing-idents (:db/ident %))) schema)]
    (when (seq schema-to-transact)
      (d/transact conn schema-to-transact))))

(defn transact [data database-name]
  (d/transact (get-connection database-name) data))

(defn query [query database-name]
  (d/q {:query query}
       (d/db (get-connection database-name))))

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