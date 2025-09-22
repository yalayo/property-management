(ns app.storage.datahike
  (:require [datahike.api :as d]
            [datahike-jdbc.core]
            [com.brunobonacci.mulog :as mu])
  (:import (clojure.lang ExceptionInfo)))

(defn base-config [database-name]
  (let [environment (System/getenv "ENVIRONMENT")]
    (case environment
      "local" #_{:store {:backend :mem :id database-name}} {:store {:backend :file :path (str "tmp/" database-name)}}
      {:store {:backend :jdbc
               :dbtype "postgresql"
               :host (System/getenv "DB_HOST")
               :port 5432
               :user "user"
               :password (System/getenv "DB_PASSWORD")
               :table database-name
               :dbname "property-management"
               :pool-options {:maximumPoolSize 10
                              :minimumIdle 2
                              :idleTimeout 60000
                              :maxLifetime 1800000
                              :connectionTimeout 30000}}})))

(defn init [database-name] 
  (let [cfg (base-config database-name)] 
    (try 
      (when-not (d/database-exists? cfg)
                 (d/create-database cfg)) 
      (let [conn (d/connect cfg)]
                 (mu/log ::datahike-conn-started :db database-name)
                 conn) 
      (catch ExceptionInfo e 
        (mu/log ::datahike-conn-error :db database-name :exception e) 
        (throw e)))))

(defn stop [conn]
  (try
    (d/release conn)
    (mu/log ::datahike-conn-closed)
    (catch Exception e
      (mu/log ::datahike-conn-close-error :exception e))))

(defn get-config [database-name]
  (let [environment (System/getenv "ENVIRONMENT")]
    (case environment
      "local" #_{:store {:backend :mem :id database-name}} {:store {:backend :file :path (str "tmp/" database-name)}}
      {:store {:backend :jdbc
               :dbtype "postgresql"
               :host (System/getenv "DB_HOST")
               :port 5432
               :user "user"
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

(defn transact-schema
  [conn schema]
  (let [existing (into #{}
                       (map first
                            (d/q '[:find ?ident
                                   :where [?e :db/ident ?ident]]
                                 (d/db conn))))
        schema-to-transact (remove #(contains? existing (:db/ident %)) schema)]
    (when (seq schema-to-transact)
      (d/transact conn schema-to-transact))))

(defn transact [conn data]
  (d/transact conn data))

(defn query [conn q]
  (d/q {:query q} (d/db conn)))

(defn query-with-parameter [conn q value]
  (d/q {:query q} (d/db conn) value))

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