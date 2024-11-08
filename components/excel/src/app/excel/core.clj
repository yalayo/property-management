(ns app.excel.core
  (:require [dk.ative.docjure.spreadsheet :as docj]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn get-cell-value [cell]
  (let [cell-type (str (.getCellType cell))]  ; Convert cell type to a string for matching
    (case cell-type
      "NUMERIC" (.getNumericCellValue cell)
      "STRING" (.getStringCellValue cell)
      "BOOLEAN" (.getBooleanCellValue cell)
      "FORMULA" (.getCellFormula cell)
      "BLANK" nil
      (throw (IllegalArgumentException. (str "No matching clause: " cell-type))))))

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

(defn process [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        sheets (docj/sheet-seq workbook)
        filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
    (into [] (map (fn [sheet]
                    (let [last-name (subs (.getSheetName sheet) 3)
                          street (get-cell-value (docj/select-cell "A2" sheet))]
                      {:last-name last-name :street street})) filtered))))

(comment
  (process-details (io/input-stream "D:/personal/projects/inmo-verwaltung/code/property-management/components/excel/resources/test.xlsx"))

  (let [input-stream (io/input-stream "D:/personal/projects/inmo-verwaltung/code/property-management/components/excel/resources/NK_ 2023_kuni.xlsx")
        workbook (docj/load-workbook input-stream)
        sheets (docj/sheet-seq workbook) 
        filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
    (into [] (map (fn [sheet]
           (let [last-name (subs (.getSheetName sheet) 3)
                 street (get-cell-value (docj/select-cell "A2" sheet))]
             {:last-name last-name :street street})) filtered)))

)