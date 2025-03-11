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

(def bank-schema [{:db/ident :iban
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :bank-name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn create-property [name]
  (storage/transact [{:id (str (java.util.UUID/randomUUID)) :name name}] "properties"))

(defn list-properties []
  (let [data (storage/query "[:find ?id ?name :where [?e :id ?id] [?e :name ?name]]" "properties")]
    (map (fn [[id name]]
           {:id id :name name})
         data)))

(defn get-property-by-name [name]
  (let [query (str "[:find ?id ?name :where [?e :id ?id] [?e :name ?name] [(= ?name \"" name "\")]]")
        data (storage/query query "properties")]
    (map (fn [[id name]]
           {:id id :name name})
         data)))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "properties")

  )

(comment
  "Check that add properties works ok"
  (list-properties)
  (get-property-by-name "ieo")
  )