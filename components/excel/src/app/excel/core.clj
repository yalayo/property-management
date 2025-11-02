(ns app.excel.core
  (:require [dk.ative.docjure.spreadsheet :as docj]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.async :refer [go chan close! >!! <!!]]
            [clojure.core.async :as async])
  (:import [java.util.concurrent Executors Callable]))

(defn get-formula-value [cell]
  (let [workbook (.getWorkbook (.getSheet cell))
        evaluator (.createFormulaEvaluator (.getCreationHelper workbook))
        evaluated-cell (.evaluate evaluator cell)
        cell-type (str (.getCellType evaluated-cell))]
    (case cell-type
      "NUMERIC" (.getNumericCellValue cell)
      "STRING" (.getStringCellValue cell)
      "BOOLEAN" (.getBooleanCellValue cell)
      "BLANK" nil)))

(defn get-cell-value [cell]
  (when (some? cell)
    (let [cell-type (str (.getCellType cell))
          cell-address (.formatAsString (.getAddress cell))
          sheet-name (.getSheetName (.getSheet cell))]
      (try
        (case cell-type
          "NUMERIC" (.getNumericCellValue cell)
          "STRING"  (if (or (str/starts-with? cell-address "C") (str/starts-with? cell-address "E"))
                      {:error true :message "Der Inhalt dieser Zelle kann kein Text sein" :cell cell-address :sheet sheet-name}
                      (.getStringCellValue cell))
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell cell-address :sheet sheet-name})))))

(defn get-header-cell-value [cell]
  (when (some? cell)
    (let [cell-type (str (.getCellType cell))
          cell-address (.formatAsString (.getAddress cell))
          sheet-name (.getSheetName (.getSheet cell))]
      (try
        (case cell-type
          "NUMERIC" (.getNumericCellValue cell)
          "STRING"  (str/trim (.getStringCellValue cell))
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell cell-address :sheet sheet-name})))))

(defn format-headers [headers]
  (zipmap (map #(keyword (str %)) (range 1 (inc (count headers))))
          headers))

(defn get-headers-content [cells]
  (map get-header-cell-value cells))

(defn get-content [cells]
  (mapv get-cell-value cells))

(defn format-content [data size]
  (let [items (into [] (map #(keyword (str (inc %))) (range size)))
        content (partition size data)]
    (mapv #(zipmap items %) content)))

(defn get-cell-data [cell name required?]
  (when (some? cell)
    (let [cell-type (str (.getCellType cell))
          cell-address (.formatAsString (.getAddress cell))
          sheet-name (.getSheetName (.getSheet cell))]
      (try
        (case cell-type
          "NUMERIC" {name (.getNumericCellValue cell)}
          "STRING" {name (.getStringCellValue cell)}
          "BOOLEAN" {name (.getBooleanCellValue cell)}
          "FORMULA" {name (get-formula-value cell)}
          "BLANK" (when required? (throw (IllegalArgumentException. "Diese Zelle darf nicht leer sein")))
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell cell-address :sheet sheet-name})))))

(def attributes [{:name "last-name" :cell "B3"}
                 {:name "date" :cell "D2"}
                 {:name "family" :cell "B3"}
                 {:name "property-id" :cell "C3"}
                 {:name "street" :cell "A2"}
                 {:name "location" :cell "B2"}
                 {:name "total-costs" :cell "I2"}
                 {:name "prepayment" :cell "I3"}
                 {:name "heating-costs" :cell "I4"}
                 {:name "total" :cell "I5"}
                 {:name "iban" :cell "L2"}
                 {:name "bank-name" :cell "L3"}
                 {:name "refund" :cell "I6"}
                 {:name "year" :cell "M11"}
                 {:name "property-name" :cell "L5" :required true}
                 {:name "property-address" :cell "M5" :required true}
                 {:name "property-apartment" :cell "M6" :required true}
                 {:name "property-time-period" :cell "M7" :required true}
                 {:name "property-calculated-days" :cell "M8" :required true}
                 {:name "property-days-per-person" :cell "M9"}])

(def client-attributes [{:name "last-name" :cell "B3"}
                        {:name "IBAN" :cell "L2"}
                        {:name "BANK" :cell "L3"}])

(def apartment-data [{:name "last-name" :cell "B3"}
                     {:name "property-id" :cell "C3"}])

(def property-bank-data [{:name "IBAN" :cell "L2"}
                         {:name "BANK" :cell "L3"}])

(defn get-attribute-value [data]
  (let [sheet (:sheet data)
        name (keyword (:name data))
        cell (:cell data)
       required? (:required data)]
    (get-cell-data (docj/select-cell cell sheet) name required?)))

(defn contains-error? [element]
  (cond
    (map? element) (:error element)
    (sequential? element) (some contains-error? element)
    :else false))

(defn collect-errors [x]
  (cond
    (map? x) (if (:error x) [x] [])
    (sequential? x) (mapcat collect-errors x)
    :else []))

(defn get-tenant-data [general headers content]
  (let [with-headers (into {} {:headers (format-headers headers)})
        with-content (into with-headers {:content (format-content content (count headers))})
        with-id (into with-content {:id (str (java.util.UUID/randomUUID))})]
    (into with-id general)))

(defn process-sheet [sheet workbook]
  (let [headers (get-headers-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
        content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
        data (map #(assoc % :sheet sheet) attributes)
        result (map get-attribute-value data)
        content-errors (collect-errors content)
        result-errors  (collect-errors result)
        errors         (concat content-errors result-errors)]
    (if (seq errors)
      errors
      (get-tenant-data result headers content))))

(defn extract-property-bank-data [sheet workbook]
  (let [headers (get-headers-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
        content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
        bank-data (map #(assoc % :sheet sheet) property-bank-data)
        property-bank-data-result (map get-attribute-value bank-data)
        content-errors (filter contains-error? content)]
    (println "Property bank data: " property-bank-data-result)
    (if (some #(:error %) property-bank-data-result)
      (into [] (concat content-errors (filter :error property-bank-data-result)))
      (get-tenant-data property-bank-data-result headers content))))

(defn extract-client-data [sheet workbook]
    (let [headers (get-headers-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
         content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
         client-data (map #(assoc % :sheet sheet) client-attributes)
         client-result (map get-attribute-value client-data)
         content-errors (filter contains-error? content)]
     (println "Client attributes: " client-result)
     (if (some #(:error %) client-result)
       (into [] (concat content-errors (filter :error client-result)))
       (get-tenant-data client-result headers content))))

(defn list-tenants [input-stream]
  (let [workbook (docj/load-workbook-from-stream input-stream)
        sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))
        tenants (map (fn [sheet-name] {:id (str (java.util.UUID/randomUUID)) :last-name (subs (.getSheetName sheet-name) 3)}) sheets)]
    (println "Tenants: " tenants)
    {:tenants tenants}))

#_(defn process [input-stream]
  (with-open [workbook (docj/load-workbook input-stream)]
    (let [sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))]
      (doall (map #(process-sheet % workbook) sheets)))))

(defn process [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))
        pool (Executors/newFixedThreadPool 30)
        result-chan (chan 10)]
    ;; Process all sheets in parallel
    (doseq [sheet sheets]
      (.submit pool
               (reify Callable
                 (call [_]
                   (let [result (process-sheet sheet workbook)]
                     (async/>!! result-chan result)
                     nil)))))

    ;; Close the channel after all threads are spawned
    (go
      (.shutdown pool) ;; Shutdown the thread pool
      (.awaitTermination pool 5 java.util.concurrent.TimeUnit/SECONDS) ;; Wait for all tasks to complete
      (close! result-chan))

    ;; Collect results
    (<!! (async/into [] result-chan))))

(defn extract [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))
        pool (Executors/newFixedThreadPool 30)
        result-chan (chan 10)]
    ;; Process all sheets in parallel
    (doseq [sheet sheets]
      (.submit pool
               (reify Callable
                 (call [_]
                   (let [result (extract-client-data sheet workbook)]
                     (async/>!! result-chan result)
                     nil)))))

    ;; Close the channel after all threads are spawned
    (go
      (.shutdown pool) ;; Shutdown the thread pool
      (.awaitTermination pool 5 java.util.concurrent.TimeUnit/SECONDS) ;; Wait for all tasks to complete
      (close! result-chan))

    ;; Collect results
    (<!! (async/into [] result-chan))))

(defn extract-bank [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))
        pool (Executors/newFixedThreadPool 30)
        result-chan (chan 10)]
    ;; Process all sheets in parallel
    (doseq [sheet sheets]
      (.submit pool
               (reify Callable
                 (call [_]
                   (let [result (extract-property-bank-data sheet workbook)]
                     (async/>!! result-chan result)
                     nil)))))

    ;; Close the channel after all threads are spawned
    (go
      (.shutdown pool) ;; Shutdown the thread pool
      (.awaitTermination pool 5 java.util.concurrent.TimeUnit/SECONDS) ;; Wait for all tasks to complete
      (close! result-chan))

    ;; Collect results
    (<!! (async/into [] result-chan))))

(comment
  (require '[datahike.api :as d])

  (def cfg {:store {:backend :mem :id "excel-data"}})

  ;; Create the DB
  (d/create-database cfg)
  (def conn (d/connect cfg))

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

     {:db/ident       :bill/year
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one}

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

  (def schema (concat tenant-schema bill-schema cost-item-schema))

  (d/transact conn {:tx-data schema})

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

  (defn flatten-mixed [coll]
    (vec
     (mapcat
      (fn [x]
        (cond
          (map? x) [x]
          (sequential? x) (flatten-mixed x)  ;; recursively flatten deeper sequences
          :else []))
      coll)))

  (let [data (process (io/input-stream "D:/personal/projects/inmo-verwaltung/work-data/for-the-letters/to_validate.xlsx"))
        #_#_tenants (vec (map make-tenant (remove-nils data)))
        flattened (flatten-mixed data)
        result (map #(assoc % :year "year") flattened)
        tenants (vec (map make-tenant (remove-nils result)))]


    (d/transact conn tenants)
    #_(println "Data: " tenants)
    #_tenants)
  
  (d/q '[:find ?property-id ?property-name
         :where
         [?b :bill/property-id ?property-id]
         [?b :bill/property-name ?property-name]]
       @conn)
  
  (map (fn [[id name]]
         {:property/id id
          :property/name name})
       (d/q '[:find ?id ?name
              :where
              [?b :bill/property-id ?id]
              [?b :bill/property-name ?name]]
            @conn))
  
  ;; without prefix
  (into [] (map (fn [[id name]]
         {:id id
          :name name})
       (d/q '[:find ?id ?name ?street
              :where
              [?b :bill/property-id ?id]
              [?b :bill/property-name ?name]
              [?b :tenant/street ?street]]
            @conn)))
  
  (map (fn [[id name street location]]
         {:id id
          :name name
          :street street
          :location location})
       (d/q '[:find ?id ?name ?street ?location
              :where
              [?t :tenant/bills ?b]
              [?t :tenant/street ?street]
              [?t :tenant/location ?location]
              [?b :bill/property-id ?id]
              [?b :bill/property-name ?name]]
            @conn))
  
  (map (fn [[id last-name street location]]
         {:id id
          :lastname last-name
          :street street
          :location location})
       (d/q '[:find ?id ?last-name ?street ?location
              :where
              [?t :tenant/id ?id]
              [?t :tenant/last-name ?last-name]
              [?t :tenant/street ?street]
              [?t :tenant/location ?location]]
            @conn))

  ;; Queries
  ;; Get all bills with total > 0
  (d/q '[:find ?bill ?total
         :where [?bill :bill/total ?total]
         [(> ?total 0)]]
       @conn)

  ;; Or fetch all cost items for a given bill
  (d/pull @conn
          '[:bill/id
            {:bill/cost-items [:cost-item/name :cost-item/result]}]
          [:bill/id (java.util.UUID/fromString "54ecf705-42f7-4f36-b6b6-ed0ce8f54ec6")])

  ;; All families with total > 0
  (d/q '[:find ?fam ?bill ?total
         :where
         [?fam :tenant/last-name ?name]
         [?fam :tenant/bills ?bill]
         [?bill :bill/total ?total]
         [(> ?total 0)]]
       @conn)


  ;; Get all bills and cost items for a tenant
  (d/pull @conn
          '[:tenant/last-name
            {:tenant/bills
             [:bill/property-time-period
              :bill/total
              {:bill/cost-items [:cost-item/name :cost-item/result]}]}]
          [:tenant/last-name "Fam.Brusberg"])

  ;; Get all bills and cost items for a property by id
  (d/pull @conn
          '[:bill/property-id
            {:tenant/bills
             [:bill/property-time-period
              :bill/total
              {:bill/cost-items [:cost-item/name :cost-item/result]}]}]
          [:bill/property-id "01-WH1-EG-L"])



  ;; Get the bill given an id
  (first
   (d/q '[:find (pull ?b [* {:bill/cost-items [*]}])
          :in $ ?pid
          :where [?b :bill/property-id ?pid]]
        @conn
        "01-WH1-EG-L"))
  ;;
  (first
   (d/q '[:find (pull ?b [* {:bill/cost-items [*]
                             :_tenant/bills [*]}])
          :in $ ?pid
          :where [?b :bill/property-id ?pid]]
        @conn
        "01-WH1-EG-L"))

  ;; Get the tenant by last-name
  (d/q '[:find (pull ?t [*])
         :in $ ?last-name
         :where [?t :tenant/last-name ?last-name]]
       @conn "Leer-Friese")

  ;; Nested pull
  (d/q '[:find (pull ?t
                     [:tenant/last-name
                      :tenant/location
                      {:tenant/bills
                       [:bill/property-id
                        :bill/property-name
                        :bill/property-address
                        :bill/total
                        {:bill/cost-items
                         [:cost-item/name
                          :cost-item/result]}]}])
         :in $ ?last-name
         :where [?t :tenant/last-name ?last-name]]
       @conn "Leer-Friese")

  (d/q '[:find (pull ?b [*])
         :in $ ?year
         :where
         [?b :bill/year ?year]]
       @conn
       "2024")


  (d/delete-database cfg)

  )