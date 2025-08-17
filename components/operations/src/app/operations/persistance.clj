(ns app.operations.persistance
  (:require [app.storage.interface :as storage]
            [datahike.api :as d]))

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
              :db/cardinality :db.cardinality/one}
             {:db/ident :ocupancy/start
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one}
             {:db/ident :ocupancy/end
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one}
             {:db/ident :ocupancy/year
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :ocupancy/tenant
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :ocupancy/apartment
              :db/valueType :db.type/string
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

(defn store-ocupancy [data]
  (let [year (:year data)
        tenant (:tenant data)
        apartment (:apartment data)
        value (:value data)
        kind (:kind data)]
    (case kind
     :start (storage/transact [{:ocupancy/start value :ocupancy/year year :ocupancy/tenant tenant :ocupancy/apartment apartment}] "operations")
     :end (storage/transact [{:ocupancy/end value :ocupancy/year year :ocupancy/tenant tenant :ocupancy/apartment apartment}] "operations"))))

(comment
  (defn get-connection []
    (let [config {:store {:backend :mem :id "test"}}]
      (when-not (d/database-exists? config)
        (d/create-database config))
      (d/connect config)))



  (def conn (get-connection))

  (d/transact conn schema)

  ;; Getting the idents of the schema already in the database
  (into #{} (map first (d/q '[:find ?ident
                              :where [?e :db/ident ?ident]]
                            (d/db conn))))
  
  (defn store-expense [data]
    (let [expense-id (str (java.util.UUID/randomUUID))
          expense-data [{:expense/id expense-id
                         :expense/category (:category data)
                         :expense/year (:year data)
                         :expense/property (:property data)
                         :expense/amount (:amount data)}]]
      (d/transact conn expense-data)))
  
  ;; Store an expense
  (store-expense {:category :garbage :year "2022" :property "property-2" :amount 120.80})
  
  (d/q '[:find [(pull ?tg [*]) ...]
         :where
         [?tg :expense/property ?tgn]]
       @conn)
  
  ;; Get category-db-id and amount given a property-id
  (d/q '[:find ?property ?category ?amount
         :where [?e :expense/property "property-2"] [?e :expense/property ?property] [?e :expense/category ?category] [?e :expense/amount ?amount]]
       (d/db conn))
  )