(ns app.operations.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :iban
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn transact-schema []
  (storage/transact-schema schema "operations"))

(defn create-operations [data]
  (let [operations-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact operations-data "operations")))

(defn list-operations []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :name _]] "operations"))