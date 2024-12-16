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
      (let [cell-type (str (.getCellType cell))]  ; Convert cell type to a string for matching
        (case cell-type
          "NUMERIC" (.getNumericCellValue cell)
          "STRING" (.getStringCellValue cell)
          "BOOLEAN" (.getBooleanCellValue cell)
          "FORMULA" (get-formula-value cell)
          "BLANK" nil
          (throw (IllegalArgumentException. (str "No matching clause: " cell-type)))))))

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

(defn process [input-stream]
  (try
    (let [workbook (docj/load-workbook input-stream)
          sheets (docj/sheet-seq workbook)
          filtered (filter #(str/starts-with? (.getSheetName %) "W") sheets)]
      (into [] (map (fn [sheet]
                      (let [last-name (get-cell-value (docj/select-cell "B3" sheet))
                            property-id (get-cell-value (docj/select-cell "C3" sheet))
                            street (get-cell-value (docj/select-cell "A2" sheet))
                            location (get-cell-value (docj/select-cell "B2" sheet))
                            total-costs (get-cell-value (docj/select-cell "I2" sheet))
                            prepayment (get-cell-value (docj/select-cell "I3" sheet))
                            heating-costs (get-cell-value (docj/select-cell "I4" sheet))
                            total (get-cell-value (docj/select-cell "I5" sheet))
                            iban (get-cell-value (docj/select-cell "L2" sheet))
                            bank-name (get-cell-value (docj/select-cell "L3" sheet))
                            refund? (get-cell-value (docj/select-cell "I6" sheet))
                            headers (get-content (docj/select-name workbook (str "h" (.getSheetName sheet))))
                            content (get-content (docj/select-name workbook (str "t" (.getSheetName sheet))))]
                        (println "TENANT: " last-name)
                        {:tenant-id (str (java.util.UUID/randomUUID))
                         :last-name last-name
                         :street street
                         :location location
                         :total-costs total-costs
                         :prepayment prepayment
                         :heating-costs heating-costs
                         :total total
                         :payment-info {:iban iban :bank-name bank-name}
                         :refund refund?
                         :headers (format-headers headers)
                         :content (format-content content (count headers))
                         :property-info {:id property-id
                                         :name (get-cell-value (docj/select-cell "L5" sheet))
                                         :address (get-cell-value (docj/select-cell "M5" sheet))
                                         :apartment (get-cell-value (docj/select-cell "M6" sheet))
                                         :time-period (get-cell-value (docj/select-cell "M7" sheet))
                                         :calculated-days (get-cell-value (docj/select-cell "M8" sheet))
                                         :days-per-person (get-cell-value (docj/select-cell "M9" sheet))}})) filtered)))
    (catch  Exception e {:error true :message (.getMessage e)})))

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