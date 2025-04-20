(ns app.tenant.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :lastname
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :email
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :phone
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}])

(defn transact-schema []
  (storage/transact-schema schema "tenants"))

(defn create-tenant [data]
  (let [tenant-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact tenant-data "tenants")))

(defn list-tenants []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :name _]] "tenants"))