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
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn create-feature-flag [name]
  (storage/transact [{:id (java.util.UUID/randomUUID) :name name :enabled false}] "feature-flags"))

(defn enable-feature-flag [id]
  (storage/transact [{:id id :enabled true}] "feature-flags"))

(defn list-feature-flags []
  (storage/query
   "[:find ?id ?name ?enabled :where [?e :id ?id] [?e :name ?name] [?e :enabled ?enabled]]" ""))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "flags")
  )