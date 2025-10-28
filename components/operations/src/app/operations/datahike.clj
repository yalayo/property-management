(ns app.operations.datahike
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

(defn storage [conn]
  {:conn conn
   :transact transact
   :query query
   :query-with-parameter query-with-parameter})

(defn init [database-name schema] 
  (let [cfg (base-config database-name)] 
    (try 
      (when-not (d/database-exists? cfg)
                 (d/create-database cfg)) 
      (let [conn (d/connect cfg)] 
        (mu/log ::datahike-conn-started :db database-name)
        (when schema
          (transact-schema conn schema))
        (storage conn)) 
      (catch ExceptionInfo e 
        (mu/log ::datahike-conn-error :db database-name :exception e) 
        (throw e)))))

(defn stop [conn]
  (try
    (d/release conn)
    (mu/log ::datahike-conn-closed)
    (catch Exception e
      (mu/log ::datahike-conn-close-error :exception e))))