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
                      {:error true :message "Der Inhalt dieser Zelle kann kein Text sein" :cell-address cell-address :sheet-name sheet-name}
                      (.getStringCellValue cell))
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address :sheet-name sheet-name})))))

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
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address :sheet-name sheet-name})))))

(defn format-headers [headers]
  (zipmap (map #(keyword (str %)) (range 1 (inc (count headers))))
          headers))

(defn get-headers-content [cells]
  (map get-header-cell-value cells))

(defn get-content [cells]
  (map get-cell-value cells))

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
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address :sheet-name sheet-name})))))

(def attributes [{:name "last-name" :cell "B3"}
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

(defn get-attribute-value [data]
  (let [sheet (:sheet data)
        name (keyword (:name data))
        cell (:cell data)
       required? (:required data)]
    (get-cell-data (docj/select-cell cell sheet) name required?)))

(defn contains-error? [element]
  (and (map? element) (:error element)))

(defn get-tenant-data [general headers content]
  (let [with-headers (into {} {:headers (format-headers headers)})
        with-content (into with-headers {:content (format-content content (count headers))})
        with-id (into with-content {:tenant-id (str (java.util.UUID/randomUUID))})]
    (into with-id general)))

(defn process-sheet [sheet]
  (let [workbook (.getWorkbook sheet) 
        headers (get-headers-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
        content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
        data (map #(assoc % :sheet sheet) attributes)
        result (eduction (map get-attribute-value data))
        result-errors (filter :error result)
        content-errors (filter contains-error? content)]
    (if (seq result-errors)
      (lazy-cat content-errors result-errors)
      (get-tenant-data result headers content))))

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
  
(defn process [input-stream]
  (let [workbook (docj/load-workbook-from-stream input-stream)
        sheets (filter #(str/starts-with? (.getSheetName %) "W") (docj/sheet-seq workbook))
        cpu-cores (.. Runtime getRuntime availableProcessors)
        pool-size (max 1 (min cpu-cores 10)) ;; Dynamically adjust threads
        pool (Executors/newFixedThreadPool pool-size)
        result-chan (chan 20)]
          ;; Process all sheets in parallel
    (doseq [sheet sheets]
      (let [result (process-sheet sheet)]
        (println "Sheet result: " result)
        (async/>!! result-chan result)
        (System/gc)
        nil)
      #_(.submit pool
               (reify Callable
                 (call [_]
                   (let [result (process-sheet sheet)]
                     (async/>!! result-chan result)
                     (System/gc)
                     nil)))))
  
          ;; Close the channel after all threads are spawned
    (go
      #_(.shutdown pool) ;; Shutdown the thread pool
      #_(.awaitTermination pool 5 java.util.concurrent.TimeUnit/SECONDS) ;; Wait for all tasks to complete
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

(comment 
  (process (io/input-stream "D:/personal/projects/inmo-verwaltung/work-data/for-the-letters/NK_ 2023_kuni.xlsx"))
  )