(ns app.excel.core
  (:require [dk.ative.docjure.spreadsheet :as docj]
            [clojure.java.io :as io]
            [clojure.string :as str]))

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
          cell-address (.formatAsString (.getAddress cell))]
      (try
        (case cell-type
          "NUMERIC" (.getNumericCellValue cell)
          "STRING"  (if (or (str/starts-with? cell-address "C") (str/starts-with? cell-address "E"))
                     {:error true :message "The cell can not be an string" :cell-address cell-address}
                    (.getStringCellValue cell))
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address})))))
;;(.getStringCellValue cell)
(defn format-headers [headers]
  (zipmap (map #(keyword (str %)) (range 1 (inc (count headers))))
          headers))

(defn get-content [cells]
  (map get-cell-value cells))

(defn format-content [data size]
  (let [items (into [] (map #(keyword (str (inc %))) (range size)))
        content (partition size data)]
    (mapv #(zipmap items %) content)))

(defn process-details [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        headers (get-content (-> workbook 
                                 (docj/select-name "headers")))
        data (get-content (-> workbook
                    (docj/select-name "table1")))]
    {:headers headers
     :content (format-content data (count headers))}))

(defn get-cell-data [cell name required?]
  (when (some? cell)
    (let [cell-type (str (.getCellType cell))
          cell-address (.formatAsString (.getAddress cell))]
      (try
        (case cell-type
          "NUMERIC" {name (.getNumericCellValue cell)}
          "STRING" {name (.getStringCellValue cell)}
          "BOOLEAN" {name (.getBooleanCellValue cell)}
          "FORMULA" {name (get-formula-value cell)}
          "BLANK" (if required?
                    (throw (IllegalArgumentException. (str "Error: The cell is empty: " cell-address)))
                    {name nil})
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address})))))

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
                 {:name "property-days-per-person" :cell "M9" :required true}])

(defn get-attribute-value [data]
  (let [sheet (:sheet data)
        name (keyword (:name data))
        cell (:cell data)
       required? (:required data)]
    (get-cell-data (docj/select-cell cell sheet) name required?)))

(defn contains-error? [element]
  (and (map? element) (:error element)))

(defn process [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (docj/sheet-seq workbook)
        filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
    (vec (flatten (map (fn [sheet]
           (let [headers (get-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
                 content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
                 data (map #(assoc % :sheet sheet) attributes)
                 result (map get-attribute-value data)
                 content-errors (into [] (filter contains-error? content))]
             (if (some #(:error %) result)
               (into [] (concat content-errors (filter :error result)))
               (into {:tenant-id (str (java.util.UUID/randomUUID))} result)))) filtered)))))

 ;;content-errors (when (contains-error? content) (filter contains-error? content))

(comment 
  (process (io/input-stream "D:/Trabajo/to_validate_wrong_data_in_column.xlsx"))
  )


(comment
  (process-details (io/input-stream "D:/personal/projects/inmo-verwaltung/code/property-management/components/excel/resources/test.xlsx"))

  (let [input-stream (io/input-stream "D:/personal/projects/inmo-verwaltung/code/property-management/components/excel/resources/NK_ 2023_kuni_2.xlsx")
        workbook (docj/load-workbook input-stream)
        sheets (docj/sheet-seq workbook) 
        filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
    (into [] (map (fn [sheet]
           (let [last-name (subs (.getSheetName sheet) 3)
                 street (get-cell-value (docj/select-cell "A2" sheet))
                 location (get-cell-value (docj/select-cell "B2" sheet))
                 headers (get-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
                 content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))]
             {:tenant-id (str (java.util.UUID/randomUUID))
              :last-name last-name 
              :street street
              :location location
              :headers headers 
              :content (format-content content (count headers))})) filtered)))

)