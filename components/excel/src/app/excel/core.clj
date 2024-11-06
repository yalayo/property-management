(ns app.excel.core
  (:require [dk.ative.docjure.spreadsheet :as docj]
            [clojure.java.io :as io])
  (:import [org.apache.poi.ss.usermodel CellType]))

(defn get-content [cells]
  (map (fn [cell]
         (let [cell-type (str (.getCellType cell))]  ; Convert cell type to a string for matching
           (case cell-type
             "NUMERIC" (.getNumericCellValue cell)
             "STRING" (.getStringCellValue cell)
             "BOOLEAN" (.getBooleanCellValue cell)
             "FORMULA" (.getCellFormula cell)
             "BLANK" nil
             (throw (IllegalArgumentException. (str "No matching clause: " cell-type)))))) cells))

(defn process-details [input-stream]
  (let [workbook (docj/load-workbook input-stream)
        headers (get-content (-> workbook 
                                 (docj/select-name "headers")))
        content (get-content (-> workbook
                    (docj/select-name "table1")))]
    {:headers headers
     :content (partition (count headers) content)}))

(comment
  (process-details (io/input-stream "D:/personal/projects/inmo-verwaltung/code/property-management/components/excel/resources/test.xlsx"))
  )