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

(defn assign-tenant [data]
  (let [apartment-id (:apartment-id data)
        tenant-id (:tenant-id data)
        persist-data [[:db/add [:id apartment-id] :tenant tenant-id]]]
    (storage/transact persist-data "apartments")))

(defn list-apartments [storage]
  (let [conn (:conn storage)
        query (:query storage)
        result (query conn '[:find ?apartment-id ?apartment-code ?tenant-id ?tenant-name
                             :where
                             [?t :tenant/id ?tenant-id]
                             [?t :tenant/last-name ?tenant-name]
                             [?t :tenant/bills ?b]
                             [?b :bill/property-id ?apartment-id]
                             [?b :bill/property-apartment ?apartment-code]])]
    (into [] (map (fn [[apartment-id apartment-code tenant-id tenant-name]]
                    {:id apartment-id
                     :code apartment-code
                     :tenant {:id tenant-id :name tenant-name}}) result))))