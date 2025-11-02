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

(defn list-accounts [storage]
  (let [conn (:conn storage)
        query (:query storage)
        result (query conn '[:find ?iban ?bank
                             :with ?b
                             :where
                             [?b :bill/iban ?iban]
                             [?b :bill/bank-name ?bank]])]
    (into [] (distinct (map (fn [[iban bank]]
                    {:iban iban
                     :bank bank}) result)))))