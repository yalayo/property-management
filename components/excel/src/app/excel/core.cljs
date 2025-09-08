(ns app.excel.core
  (:require ["exceljs" :as excel]
            [clojure.string :as str]))

(defn get-cell-value [cell]
  (when cell
    (let [value (.-value cell)]
      (cond
        (map? value) ;; formulas in exceljs come as {:formula "A1*2" :result 42}
        (or (:result value) (:formula value))

        (string? value) value
        (number? value) value
        (boolean? value) value
        :else nil))))

(defn get-headers [^js worksheet name-range]
  (->> (.getRow worksheet name-range)
       (.-values)
       (map #(some-> % str str/trim))))

;; if you don’t have named ranges, iterate the first row
(defn get-headers-from-row [^js worksheet row-num]
  (->> (.getRow worksheet row-num)
       (.-values)
       (map #(when % (str/trim (str %))))))

;; Should't change much
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

;; get the value of an attribute
(defn get-attribute-value [^js worksheet {:keys [name cell required]}]
  (let [cell-obj (.getCell worksheet cell)
        value (get-cell-value cell-obj)]
    (if (and required (nil? value))
      {:error true :message "Required cell missing" :cell cell :sheet (.-name worksheet)}
      {name value})))

(defn process-sheet [^js worksheet]
  (let [attrs (map #(get-attribute-value worksheet %) attributes)
        errors (filter :error attrs)]
    (if (seq errors)
      errors
      {:id (random-uuid)
       :attributes (apply merge attrs)
       :headers (get-headers-from-row worksheet 1)
       :content (map (fn [row] (map get-cell-value (.-cells row)))
                     (rest (array-seq (.getRows worksheet 2 (.-rowCount worksheet)))))})))

(defn process [data]
  (let [workbook (excel/Workbook.)]
    (-> (.. workbook -xlsx (load data))
        (.then
         (fn [^js wb]
           (->> (array-seq (.-worksheets wb))
                (filter #(str/starts-with? (.-name %) "W"))
                (map process-sheet)))))))
                  
#_(defn process [data]
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