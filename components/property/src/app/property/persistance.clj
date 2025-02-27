(ns app.property.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn create-property [name]
  (storage/transact [{:id (java.util.UUID/randomUUID) :name name :enabled false}] "properties"))

(defn list-properties []
  (storage/query
   "[:find ?id ?name :where [?e :id ?id] [?e :name ?name]]" "properties"))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "properties")
  )