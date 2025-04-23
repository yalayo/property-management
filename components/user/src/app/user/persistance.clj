(ns app.user.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :email
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :identifier
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :password
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :verified
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :admin
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :test
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :disabled
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :created
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one}])

(defn transact-schema []
  (storage/transact-schema schema "users"))

(defn create-user [data]
  (let [user-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (storage/transact user-data "users")))

(defn list-users []
  (storage/query '[:find [(pull ?e [*]) ...] :where [?e :email _]] "users"))

(defn get-account [email]
  (ffirst
   (storage/query-with-parameter '[:find (pull ?e [*]) :in $ ?email :where [?e :email ?email]]
    "users"
    email)))