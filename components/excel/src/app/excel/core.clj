(ns app.excel.core
  (:require [dk.ative.docjure.spreadsheet :as docj])
  (:import [org.apache.poi.ss.usermodel CellType]))

(defn process-details [input-stream]
  (->> (docj/load-workbook input-stream)
       (docj/select-sheet "Price List")
       (docj/select-columns {:A :name, :B :price})))

(->> (docj/load-workbook-from-resource "test.xlsx")
     (docj/select-sheet "Tabelle1")
     (docj/select-columns {:A :name, :B :price}))

(->> (docj/load-workbook-from-resource "test.xlsx")
     (docj/select-sheet "Tabelle1")
     (docj/select-cell "A1"))

(-> (docj/load-workbook-from-resource "test.xlsx")
     (docj/select-name "table1"))

(let [cells (-> (docj/load-workbook-from-resource "test.xlsx")
                (docj/select-name "table1"))]
  (map (fn [cell]
         (let [cell-type (str (.getCellType cell))]  ; Convert cell type to a string for matching
           (case cell-type
             "NUMERIC" (.getNumericCellValue cell)
             "STRING" (.getStringCellValue cell)
             "BOOLEAN" (.getBooleanCellValue cell)
             "FORMULA" (.getCellFormula cell)
             "BLANK" nil
             (throw (IllegalArgumentException. (str "No matching clause: " cell-type)))))) cells))