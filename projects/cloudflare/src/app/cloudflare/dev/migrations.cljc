(ns app.cloudflare.dev.migrations
  (:require ["fs" :as fs]
            ["path" :as path]
            [clojure.string :as str]))

(def sqlite3 (js/require "sqlite3"))
(defonce db (new (.-Database sqlite3) "./dev.sqlite3"))

(def migrations-dir "./migrations")

(defn read-sql-file [filepath]
  (.toString (fs/readFileSync filepath)))

(defn get-applied-migrations []
  (js/Promise.
   (fn [resolve reject]
     (.all db "SELECT filename FROM migrations"
           (fn [err rows]
             (if err (reject err)
                 (resolve (set (map #(.-filename %) rows)))))))))

(defn apply-migration! [filename sql]
  (js/Promise.
   (fn [resolve reject]
     (.exec db sql
            (fn [err]
              (if err (reject err)
                  (.run db
                        "INSERT INTO migrations (filename) VALUES (?)"
                        #js [filename]
                        (fn [err2]
                          (if err2 (reject err2)
                              (resolve filename))))))))))

(defn run-migrations! []
  (js/Promise.
   (fn [resolve reject]
     (-> (get-applied-migrations)
         (.then
          (fn [applied]
            (let [files (->> (.readdirSync fs migrations-dir)
                             (filter #(str/ends-with? % ".sql"))
                             (sort))]
              (-> (js/Promise.all
                   (->> files
                        (remove applied)
                        (map
                         (fn [f]
                           (let [sql (read-sql-file (path/join migrations-dir f))]
                             (apply-migration! f sql)))))
                   (.then #(resolve (str "Migrations applied: " (count %))))
                   (.catch reject))))))))))