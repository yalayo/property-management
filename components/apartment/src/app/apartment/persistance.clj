(ns app.apartment.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :code
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :property
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :tenant
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}])

(defn transact-schema []
  (storage/transact-schema schema "apartments"))

(defn create-apartment [data]
  (let [apartment-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact apartment-data "apartments")))

(defn list-apartments []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :code _]] "apartments"))