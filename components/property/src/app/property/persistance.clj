(ns app.property.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :address
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :city
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :postal-code
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :units
              :db/valueType :db.type/number
              :db/cardinality :db.cardinality/one}
             {:db/ident :purchase-price
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :current-value
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}])

(def bank-schema [{:db/ident :iban
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :bank-name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}])

(defn create-property [data]
  (let [property-data [{:id (str (java.util.UUID/randomUUID))
                        :name (:property-name data)
                        :address (:property-address data)
                        :city (:property-city data)
                        :postal-code (:property-postal-code data)
                        :units (:property-units data)
                        :purchase-price (:property-purchase-price data)
                        :current-value (:property-current-value data)}]]
    (storage/transact property-data "properties")))

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