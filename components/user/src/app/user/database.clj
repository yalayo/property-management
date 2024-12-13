(ns app.user.database
  (:require [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [honey.sql :as sql]
            [next.jdbc.result-set :as rs]
            [buddy.hashers :as bh])
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.flywaydb.core Flyway)))

(def ds (connection/->pool com.zaxxer.hikari.HikariDataSource
                           {:dbtype "postgres"
                            :host (if (= (System/getenv "ENVIRONMENT") "prod") "development-db" "localhost")
                            :dbname "property-management"
                            :username "user"
<<<<<<< HEAD
                            :password "hrdata@2024"
=======
                            :password (if (= (System/getenv "ENVIRONMENT") "prod") "volley@2024" "hrdata@2024")
>>>>>>> 321cac49d062600bcd6539f10461ca8e2152d717
                            :dataSourceProperties {:socketTimeout 30}}))

(defn create-account [email password]
  (jdbc/execute-one!
   ds
   (-> {:insert-into [:accounts]
        :columns [:email :password]
        :values [[email (bh/derive password)]]
        :returning :*}
       (sql/format))
   {:builder-fn rs/as-unqualified-kebab-maps}))

;; (create-account "prueba@mail.com" "password")

(defn get-account [email]
  (jdbc/execute-one!
   ds
   (-> {:select :*
        :from [:accounts]
        :where [:= :email email]}
       (sql/format))
   {:builder-fn rs/as-unqualified-kebab-maps}))

(defn migrate-db []
  (.migrate
   (.. (Flyway/configure)
       #_(dataSource datasource)
                              ; https://www.red-gate.com/blog/database-devops/flyway-naming-patterns-matter
       (locations (into-array String ["classpath:database/migrations"]))
       (table "schema_version")
       (load))))

(defn datasource-component
  [config]
  (connection/component
   HikariDataSource
   (assoc (:db-spec config)
          :init-fn (fn [datasource]
                     (.migrate
                      (.. (Flyway/configure)
                          (dataSource datasource)
                            ; https://www.red-gate.com/blog/database-devops/flyway-naming-patterns-matter
                          (locations (into-array String ["classpath:database/migrations"]))
                          (table "schema_version")
                          (load)))))))