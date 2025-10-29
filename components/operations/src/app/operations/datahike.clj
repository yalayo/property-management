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

;; Hold connections by database name
(defonce connections (atom {}))

(defn get-conn [database-name]
  (if-let [existing (get @connections database-name)]
    existing
    (let [cfg (base-config database-name)]
      (when-not (d/database-exists? cfg)
        (d/create-database cfg))
      (let [conn (d/connect cfg)]
        (swap! connections assoc database-name conn)
        (mu/log ::datahike-conn-started :db database-name)
        conn))))

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

(defn transact [data database-name]
  (let [conn (get-conn database-name)]
    (d/transact conn data)))

(defn query [database-name q]
  (let [conn (get-conn database-name)]
    (d/q {:query q} (d/db conn))))

(defn query-with-parameter [database-name q value]
  (let [conn (get-conn database-name)]
    (d/q {:query q} (d/db conn) value)))

(defn storage [database-name]
  {:transact #(transact % database-name)
   :query #(query database-name %)
   :query-with-parameter #(query-with-parameter database-name % %2)})

;; ----------------------------------------------------------------------------
;; Lifecycle
;; ----------------------------------------------------------------------------

(defn init [database-name schema]
  (let [conn (get-conn database-name)]
    (when schema
      (transact-schema conn schema))
    (storage database-name)))

(defn stop []
  (doseq [[db-name conn] @connections]
    (try
      (d/release conn)
      (mu/log ::datahike-conn-closed :db db-name)
      (catch Exception e
        (mu/log ::datahike-conn-close-error :db db-name :exception e))))
  (reset! connections {}))