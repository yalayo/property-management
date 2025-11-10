(ns app.storage.datahike
  (:require [datahike.api :as d]
            [datahike-jdbc.core]
            [next.jdbc :as jdbc]
            [com.brunobonacci.mulog :as mu]
            [taoensso.timbre :as log])
  (:import (clojure.lang ExceptionInfo)
           [com.zaxxer.hikari HikariConfig HikariDataSource]))

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
  (println "D: " data)
  (d/transact conn data))

(defn query [conn q]
  (d/q {:query q} (d/db conn)))

(defn query-with-parameter [conn q value]
  (d/q {:query q} (d/db conn) value))

(defn storage [conn]
  {:conn conn
   :transact #(transact conn %)
   :query #(query conn %)
   :query-with-parameter #(query-with-parameter conn %1 %2)})

(defonce ^:private pool* (atom nil))

(defn create-pool []
  (if @pool*
    @pool*
    (let [jdbc-url (format "jdbc:postgresql://%s:%s/%s" (System/getenv "DB_HOST") "5432" "property-management")
          config (doto (HikariConfig.)
                   (.setJdbcUrl jdbc-url)
                   (.setUsername "user")
                   (.setPassword (System/getenv "DB_PASSWORD"))
                   (.setMaximumPoolSize 10))
          ds (HikariDataSource. config)]
      (reset! pool* {:datasource ds})
      ds)))

(defn close-pool [pool]
  (when-let [ds (:datasource pool)]
    (try
      (when (instance? HikariDataSource ds)
        (.close ^HikariDataSource ds)
        (reset! pool* nil))
      (catch Exception _))))

(defn- ensure-datahike-table-exists!
  "Creates the Datahike-compatible table with id/header/meta/val columns
   if it doesn't already exist."
  [pool database-name]
  (let [ds (:datasource pool)
        sql (format "CREATE TABLE IF NOT EXISTS public.%s (
                       id VARCHAR(100) PRIMARY KEY,
                       header BYTEA,
                       meta BYTEA,
                       val BYTEA
                     )" database-name)]
    (jdbc/execute! ds [sql])))

(defn init [database-name pool schema]
  (let [base-store {:backend :jdbc
                    :dbtype "postgresql"
                    :host (System/getenv "DB_HOST")
                    :port 5432
                    :dbname "property-management"
                    :user "user"
                    :password (System/getenv "DB_PASSWORD")
                    :table database-name
                    :pool-options {:maximumPoolSize 10
                                   :minimumIdle 2
                                   :idleTimeout 60000
                                   :maxLifetime 1800000
                                   :connectionTimeout 3000}}
        
        cfg-create {:store base-store}           ;; used only for initialization
        cfg-connect {:store (assoc base-store :connection (fn [] (.getConnection (:datasource pool))))}] 
    (when-not (d/database-exists? cfg-create)
      (mu/log ::creating-datahike-database :db database-name)
      (d/create-database cfg-create))
    
    (let [conn (d/connect cfg-create)]
      (mu/log ::datahike-conn-started :db database-name)
      (when schema
        (transact-schema conn schema))
      (storage conn))))

(defn stop [conn]
  (try
    (when conn
      (d/release conn))
    (mu/log ::datahike-conn-closed)
    (catch Exception e
      (mu/log ::datahike-conn-close-error :exception e))))

(comment 
  
  (def cfg {:store {:backend :jdbc
                    :dbtype "postgresql"
                    :host (System/getenv "DB_HOST")
                    :port 5432
                    :dbname "property-management"
                    :user "user"
                    :password (System/getenv "DB_PASSWORD")
                    :table "example"
                    :pool-options {:maximumPoolSize 10
                                   :minimumIdle 2
                                   :idleTimeout 60000
                                   :maxLifetime 1800000
                                   :connectionTimeout 30000}}})

  ;; Create the database
(d/create-database cfg)

;; Create a connection
(def conn (d/connect cfg))
  
  (def schema [{:db/ident :id
                :db/valueType :db.type/string
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one}
               {:db/ident :name
                :db/valueType :db.type/string
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one}
               {:db/ident :email
                :db/valueType :db.type/string
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one}
               {:db/ident :identifier
                :db/valueType :db.type/string
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one}
               {:db/ident :password
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one}
               {:db/ident :verified
                :db/valueType :db.type/boolean
                :db/cardinality :db.cardinality/one}
               {:db/ident :admin
                :db/valueType :db.type/boolean
                :db/cardinality :db.cardinality/one}
               {:db/ident :test
                :db/valueType :db.type/boolean
                :db/cardinality :db.cardinality/one}
               {:db/ident :disabled
                :db/valueType :db.type/boolean
                :db/cardinality :db.cardinality/one}
               {:db/ident :created
                :db/valueType :db.type/instant
                :db/cardinality :db.cardinality/one}])

(log/set-min-level! :debug)

(transact-schema conn schema)

(transact conn [{:id "45809e96-9619-4ef3-b613-f455a82a119c", :email "prueba-19@mail.com", :password "bcrypt+sha512$da9f1ab9cad58ff96aaf728737653010$12$ab754b5290460e797daf10e19dcd67fd76e4a3bb8188c89e", :created #inst "2025-11-09T11:01:48.356-00:00"}])
  
(d/transact conn [{:id "45809e96-9619-4ef3-b613-f455a82a119c", :email "prueba-19@mail.com", :password "bcrypt+sha512$da9f1ab9cad58ff96aaf728737653010$12$ab754b5290460e797daf10e19dcd67fd76e4a3bb8188c89e", :created #inst "2025-11-09T11:01:48.356-00:00"}])

(def f (future (d/transact conn [{:id "45809e96-9619-4ef3-b613-f455a82a119c", :email "prueba-19@mail.com", :password "bcrypt+sha512$da9f1ab9cad58ff96aaf728737653010$12$ab754b5290460e797daf10e19dcd67fd76e4a3bb8188c89e", :created #inst "2025-11-09T11:01:48.356-00:00"}])))
(Thread/sleep 5000)
(Thread/activeCount)
(jstack)
)