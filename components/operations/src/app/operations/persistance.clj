(ns app.operations.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :electricity}
             {:db/ident :accountability}
             {:db/ident :property-tax}
             {:db/ident :garbage}
             {:db/ident :rain-water}
             {:db/ident :waste-water}
             {:db/ident :drinking-water}
             {:db/ident :expense/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :expense/category
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :expense/year
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :expense/property
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :expense/amount
              :db/valueType :db.type/number
              :db/cardinality :db.cardinality/one}])

(defn transact-schema []
  (storage/transact-schema schema "operations"))

(defn create-operations [data]
  (let [operations-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact operations-data "operations")))

(defn list-operations []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :name _]] "operations"))

(defn store-expense [data]
  (let [expense-id (str (java.util.UUID/randomUUID))
        expense-data [{:expense/id expense-id
                       :expense/category (:category data)
                       :expense/year (:year data) 
                       :expense/property (:property data)
                       :expense/amount (:amount data)}]]
    (storage/transact expense-data "operations")))