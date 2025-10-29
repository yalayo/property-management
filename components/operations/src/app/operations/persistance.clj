(ns app.operations.persistance
  (:require [app.operations.datahike :as datahike]))

(def general-schema [{:db/ident :electricity}
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

(def tenant-schema
  [{:db/ident       :tenant/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Unique identifier for a tenant"}

   {:db/ident       :tenant/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tenant/last-name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity}

   {:db/ident       :tenant/street
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :tenant/location
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   ;; One-to-many relation to bills
   {:db/ident       :tenant/bills
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}])

(def bill-schema
  [{:db/ident       :bill/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity}

   {:db/ident       :bill/property-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity}

   {:db/ident       :bill/property-apartment
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/property-name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/property-address
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/property-time-period
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/property-calculated-days
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/property-days-per-person
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/bank-name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/iban
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/total
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/total-costs
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/prepayment
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/refund
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one}

   {:db/ident       :bill/heating-costs
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   ;; Relational links
   {:db/ident       :bill/family
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "Reference to the owning family"}

   {:db/ident       :bill/cost-items
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}])

(def cost-item-schema
  [{:db/ident       :cost-item/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :cost-item/total
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :cost-item/distribution
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :cost-item/key
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :cost-item/share
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :cost-item/result
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}])

(def schema (concat general-schema tenant-schema bill-schema cost-item-schema))

(defn init [database-name]
  (datahike/init database-name schema))

(defn stop [_]
  (datahike/stop))

(defn transact-schema []
  (datahike/transact-schema schema "operations"))

(defn create-operations [data]
  (let [operations-data (conj [] (assoc data :id (str (java.util.UUID/randomUUID))))]
    (datahike/transact operations-data "operations")))

(defn list-operations []
  (datahike/query '[:find [(pull ?e [*]) ...] :where [?e :name _]] "operations"))

(defn store-expense [data]
  (let [expense-id (str (java.util.UUID/randomUUID))
        expense-data [{:expense/id expense-id
                       :expense/category (:category data)
                       :expense/year (:year data) 
                       :expense/property (:property data)
                       :expense/amount (:amount data)}]]
    (datahike/transact expense-data "operations")))

(defn store-ocupancy [data]
  (let [year (:year data)
        tenant (:tenant data)
        apartment (:apartment data)
        value (:value data)
        kind (:kind data)]
    (case kind
     :start (datahike/transact [{:ocupancy/start value :ocupancy/year year :ocupancy/tenant tenant :ocupancy/apartment apartment}] "operations")
     :end (datahike/transact [{:ocupancy/end value :ocupancy/year year :ocupancy/tenant tenant :ocupancy/apartment apartment}] "operations"))))

(defn remove-nils [data]
  (cond
    (map? data)
    (->> data
         (remove (comp nil? val))
         (map (fn [[k v]] [k (remove-nils v)]))
         (into {}))

    (vector? data)
    (mapv remove-nils data)

    :else
    data))

(defn make-cost-item [row]
  (into {}
        (remove (comp nil? val))
        {:cost-item/name         (:1 row)
         :cost-item/total        (:2 row)
         :cost-item/distribution (:3 row)
         :cost-item/key          (:4 row)
         :cost-item/share        (:5 row)
         :cost-item/result       (:6 row)}))

(defn make-bill [data]
  (let [items (map make-cost-item (:content data))]
    (into {}
          (remove (comp nil? val))
          {:bill/id                     (java.util.UUID/randomUUID)
           :bill/property-id            (:property-id data)
           :bill/property-apartment     (:property-apartment data)
           :bill/property-name          (:property-name data)
           :bill/property-address       (:property-address data)
           :bill/property-time-period   (:property-time-period data)
           :bill/property-calculated-days (:property-calculated-days data)
           :bill/property-days-per-person (:property-days-per-person data)
           :bill/bank-name              (:bank-name data)
           :bill/iban                   (:iban data)
           :bill/total                  (:total data)
           :bill/total-costs            (:total-costs data)
           :bill/prepayment             (:prepayment data)
           :bill/refund                 (:refund data)
           :bill/heating-costs          (:heating-costs data)
           :bill/cost-items             (vec items)})))

(defn make-tenant [data]
  {:tenant/id        (java.util.UUID/randomUUID)
   #_#_:tenant/name      (:family data)
   :tenant/last-name (:last-name data)
   :tenant/street    (:street data)
   :tenant/location  (:location data)
   :tenant/bills     [(make-bill data)]})

(defn store-property-info [data]
  (let [tenants (vec (map make-tenant (remove-nils data)))]
    (datahike/transact tenants "operations")))

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