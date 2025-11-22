(ns app.excel.core
  (:require ["xlsx" :as xlsx]
            [clojure.string :as str]))

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

(defn get-cell-value [^js cell]
  (when (some? cell)
    (case (.-t cell)
      "n" (.-v cell)
      "s" (.-v cell)
      "b" (.-v cell)
      "f" (.-v cell) ;; formula already evaluated by xlsx
      nil)))

(defn get-attribute-value [^js sheet {:keys [name cell required]}]
  (let [cell-value (get-cell-value (aget sheet cell))]
    (if (and required (nil? cell-value))
      {:error true :message (str "Required cell missing: " cell) :name name}
      {name cell-value})))

(defn collect-errors [data]
  (cond
    (map? data) (if (:error data) [data] [])
    (sequential? data) (mapcat collect-errors data)
    :else []))

(defn format-headers [headers]
  (zipmap (map #(keyword (str %)) (range 1 (inc (count headers))))
          headers))

(defn format-content [data size]
  (let [keys (map #(keyword (str (inc %))) (range size))
        rows (partition size data)]
    (mapv #(zipmap keys %) rows)))

(defn get-sheet [^js wb sheet-name]
  (let [sheets (or (.-Sheets wb)
                   (.-sheets wb)
                   (aget wb "Sheets")
                   (aget wb "sheets"))]
    (aget sheets sheet-name)))

(defn process-sheet [wb sheet-name]
  (let [sheet (get-sheet wb sheet-name)]
    (if (nil? sheet)
      {:error true
       :message (str "Sheet not found: " sheet-name)}
      (let [header-keys (map :name attributes)
            attr-values (map #(get-attribute-value sheet %) attributes)
            errors (collect-errors attr-values)]
        (if (seq errors)
          errors
          (zipmap header-keys (map #(get %1 %2) attr-values header-keys)))))))

(defn load-workbook [input-stream]
  (xlsx/read input-stream #js {:type "binary"}))

(defn sheet->map [sheet]
  (js->clj sheet :keywordize-keys true))

(defn process [input-stream]
   (let [wb          (load-workbook input-stream)
         sheet-names (js->clj (.-SheetNames wb))]
     (->> sheet-names
          (filter #(str/starts-with? % "W"))       ;; only sheets starting with "W"
          (map #(process-sheet wb %))   ;; call your process function
          doall)))