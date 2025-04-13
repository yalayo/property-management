(ns app.survey.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :question/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/text
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/order
              :db/valueType :db.type/number
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/active
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/email
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/submitted-at
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/answer
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/question
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}])

(defn create-questions [questions]
  (storage/transact questions "surveys"))

(defn list-questions []
  (let [data (storage/query "[:find ?id ?name ?enabled :where [?e :id ?id] [?e :name ?name] [?e :enabled ?enabled]]" "flags")]
    (map (fn [[id name enabled]]
           {:id id :name name :enabled enabled})
         data)))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "surveys")
  )