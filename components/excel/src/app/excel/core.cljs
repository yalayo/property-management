(ns app.excel.core
  (:require ["exceljs" :as excel]))
                  
(defn process [data]
  (let [workbook (excel/Workbook.)]
    (-> (.. workbook -xlsx (load data))   ;; safer than (.load (.-xlsx workbook) data)
        (.then
         (fn [^js wb]
           ;; iterate through worksheets
           (.eachSheet wb
                       (fn [^js worksheet sheet-id]
                         (js/console.log "Sheet:" sheet-id (.-name worksheet))
                         ;; iterate through rows
                         (.eachRow worksheet
                                   (fn [row row-number]
                                     (js/console.log "Row" row-number ":" (.-values row)))))))))))