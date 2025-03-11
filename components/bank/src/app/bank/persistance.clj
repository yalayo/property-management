(ns app.bank.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn create-account [name]
  (storage/transact [{:id (str (java.util.UUID/randomUUID)) :name name}] "bank"))

#_(defn list-accounts []
  (let [data (storage/query "[:find ?id ?name :where [?e :id ?id] [?e :name ?name]]" "bank")]
    (map (fn [[id name]]
           {:id id :name name})
         data)))

(defn list-accounts []
  [{:id 1 :description "Bank account 1"}
   {:id 2 :description "Bank account 2"}
   {:id 3 :description "Bank account 3"}
   {:id 5 :description "Bank account n..."}])

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "bank")

  )

(comment
  "Check that add bank works ok"
  (list-accounts)
  )