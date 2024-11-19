(ns persistance
  (:require [datahike.api :as d]))

(def property [{:db/cardinality :db.cardinality/one
              :db/ident :property/id
              :db/unique :db.unique/identity
              :db/valueType :db.type/string}
             {:db/cardinality :db.cardinality/one
              :db/ident :property/name
              :db/unique :db.unique/identity
              :db/valueType :db.type/string}
             {:db/cardinality :db.cardinality/one
              :db/ident :property/address
              :db/unique :db.unique/identity
              :db/valueType :db.type/string}])

;; Input data
(def input-data {:last-name "Fam.Sowa", 
 :payment-info {:iban "DE08 360 201 8600 2468 2536", :bank-name "Apobank"}, 
 :street "Brandstorstr.16", 
 :total 57.54098739149322, 
 :prepayment 850.0, 
 :property-info {:id "01-WH1-EG", :name "Objekt 01", :address "Brandstor.16, 45239 Essen", :apartment "01-EG", :time-period "01.01-31.12.2023", :calculated-days 307.0, :days-per-person 307.0}, 
 :total-costs -792.4590126085068, 
 :refund true, 
 :location "45239 Essen", 
 :tenant-id "cfdb1711-8744-490f-93d5-e22e31fd5ae9", 
 :heating-costs 0.0})

;; Create a database config
(def cfg {:store {:backend :mem :id "test"}})

;; Create the database
(d/create-database cfg)

;; Create a connection
(def conn (d/connect cfg))

;; Transact property schema
(d/transact conn property)

(defn store-property [data]
  (let [property-info (:property-info data)
        id (:id property-info)
        name (:name property-info)
        address (:address property-info)]
    (d/transact conn [{:property/id id :property/name name :property/address address}])))

(defn get-properties []
  (d/q '[:find [(pull ?tg [*]) ...]
         :where
         [?tg :task-group/name ?tgn]]
       @conn))

(d/q '[:find [(pull ?tg [*]) ...]
       :where
       [?tg :property/id ?tgn]]
     @conn)

(d/pull @conn '[*] [:property/id "01-WH1-EG"])

(comment
  ;; From the input-data take what we need and store it
  (store-property input-data)
  )

