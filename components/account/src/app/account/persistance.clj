(ns app.account.persistance
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
  (storage/transact-schema schema "accounts"))

(defn create-account [data]
  (let [account-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact account-data "accounts")))

(defn list-accounts []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :name _]] "accounts"))