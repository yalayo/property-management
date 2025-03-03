(ns app.flags.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :enabled
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}])

(defn create-feature-flag [name]
  (storage/transact [{:id (str (java.util.UUID/randomUUID)) :name name :enabled false}] "flags"))

(defn enable-feature-flag [id]
  (storage/transact [{:id id :enabled true}] "flags"))

(defn list-feature-flags []
  (let [data (storage/query "[:find ?id ?name ?enabled :where [?e :id ?id] [?e :name ?name] [?e :enabled ?enabled]]" "flags")]
    (map (fn [[id name enabled]]
           {:id id :name name :enabled enabled})
         data)))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "flags")
  )