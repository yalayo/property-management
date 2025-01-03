(ns app.storage.core
  (:require [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.flywaydb.core Flyway)))

(defn datasource-component [config]
  (connection/component 
   HikariDataSource
   (assoc (:db-spec config)
          :init-fn (fn [datasource]
                     (println "Running database init")
                     (.migrate
                      (.. (Flyway/configure)
                          (dataSource datasource)
                          (locations (into-array String ["classpath:migrations"]))
                          (table "schema_version")
                          (load)))))))
