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
          "STRING" (.getStringCellValue cell)
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))
        (catch Exception e {:error true :message (.getMessage e) :cell-address cell-address})))))

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

(def table [{:name "481-Tagen" :cell "A6" :required true}
            {:name "Allgemeinstrom" :cell "A7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "A8" :required true}
            {:name "Grundsteuer" :cell "A9" :required true}
            {:name "Müllabfuhr" :cell "A10" :required true}
            {:name "Niederschlagwasser" :cell "A11" :required true}
            {:name "Schmutzwasser" :cell "A12" :required true}
            {:name "Trinkwasser" :cell "A13" :required true}
            {:name "Kostenabrech. 23" :cell "A14" :required true}
            {:name "Vorauszahlung 23" :cell "A15" :required true}
            {:name "Gutschrift 23" :cell "A16" :required true}

            {:name "nullForNow" :cell "B6" :required true}
            {:name "Allgemeinstrom" :cell "B7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "B8" :required true}
            {:name "Grundsteuer" :cell "B9" :required true}
            {:name "Müllabfuhr" :cell "B10" :required true}
            {:name "Niederschlagwasser" :cell "B11" :required true}
            {:name "Schmutzwasser" :cell "B12" :required true}
            {:name "Trinkwasser" :cell "B13" :required true}
            {:name "Kostenabrech. 23" :cell "B14" :required true}
            {:name "Vorauszahlung 23" :cell "B15" :required true}
            {:name "Gutschrift 23" :cell "B16" :required true}

            {:name "Verteiler" :cell "C6" :required true}
            {:name "Allgemeinstrom" :cell "C7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "C8" :required true}
            {:name "Grundsteuer" :cell "C9" :required true}
            {:name "Müllabfuhr" :cell "C10" :required true}
            {:name "Niederschlagwasser" :cell "C11" :required true}
            {:name "Schmutzwasser" :cell "C12" :required true}
            {:name "Trinkwasser" :cell "C13" :required true}
            {:name "Kostenabrech. 23" :cell "C14" :required true}
            {:name "Vorauszahlung 23" :cell "C15" :required true}
            {:name "Gutschrift 23" :cell "C16" :required true}

            {:name "Schlüssel" :cell "D6" :required true}
            {:name "Allgemeinstrom" :cell "D7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "D8" :required true}
            {:name "Grundsteuer" :cell "D9" :required true}
            {:name "Müllabfuhr" :cell "D10" :required true}
            {:name "Niederschlagwasser" :cell "D11" :required true}
            {:name "Schmutzwasser" :cell "D12" :required true}
            {:name "Trinkwasser" :cell "D13" :required true}
            {:name "Kostenabrech. 23" :cell "D14" :required true}
            {:name "Vorauszahlung 23" :cell "D15" :required true}
            {:name "Gutschrift 23" :cell "D16" :required true}

            {:name "Anteil" :cell "E6" :required true}
            {:name "Allgemeinstrom" :cell "E7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "E8" :required true}
            {:name "Grundsteuer" :cell "E9" :required true}
            {:name "Müllabfuhr" :cell "E10" :required true}
            {:name "Niederschlagwasser" :cell "E11" :required true}
            {:name "Schmutzwasser" :cell "E12" :required true}
            {:name "Trinkwasser" :cell "E13" :required true}
            {:name "Kostenabrech. 23" :cell "E14" :required true}
            {:name "Vorauszahlung 23" :cell "E15" :required true}
            {:name "Gutschrift 23" :cell "E16" :required true}

            {:name "365 Tagen" :cell "F6" :required true}
            {:name "Allgemeinstrom" :cell "F7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "F8" :required true}
            {:name "Grundsteuer" :cell "F9" :required true}
            {:name "Müllabfuhr" :cell "F10" :required true}
            {:name "Niederschlagwasser" :cell "F11" :required true}
            {:name "Schmutzwasser" :cell "F12" :required true}
            {:name "Trinkwasser" :cell "F13" :required true}
            {:name "Kostenabrech. 23" :cell "F14" :required true}
            {:name "Vorauszahlung 23" :cell "F15" :required true}
            {:name "Gutschrift 23" :cell "F16" :required true}

            {:name "307 Tagen" :cell "G6" :required true}
            {:name "Allgemeinstrom" :cell "G7" :required true}
            {:name "Gebäude/Haftpflicht" :cell "G8" :required true}
            {:name "Grundsteuer" :cell "G9" :required true}
            {:name "Müllabfuhr" :cell "G10" :required true}
            {:name "Niederschlagwasser" :cell "G11" :required true}
            {:name "Schmutzwasser" :cell "G12" :required true}
            {:name "Trinkwasser" :cell "G13" :required true}
            {:name "Kostenabrech. 23" :cell "G14" :required true}
            {:name "Vorauszahlung 23" :cell "G15" :required true}
            {:name "Gutschrift 23" :cell "G16" :required true}])

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

(defn process [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (docj/sheet-seq workbook)
        filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
    (vec (flatten (map (fn [sheet]
           (let [headers (get-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
                 content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))
                 data (map #(assoc % :sheet sheet) attributes)
                 result (map get-attribute-value data)]
             (if (some #(:error %) result)
               (filter :error result)
               (into {:tenant-id (str (java.util.UUID/randomUUID))} result)))) filtered)))))

(comment 
  (process (io/input-stream "D:/personal/projects/inmo-verwaltung/work-data/for-the-letters/to_validate.xlsx"))
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