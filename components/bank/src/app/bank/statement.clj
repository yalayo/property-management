(ns app.bank.statement
  (:require [clojure.string :as str])
  (:import [org.apache.pdfbox Loader]
           [org.apache.pdfbox.text PDFTextStripper]
           [java.io FileInputStream InputStream]
           [java.net URL]
           [java.io File]
           [java.lang Character]
           [java.time LocalDate]
           [java.time.format DateTimeFormatter]
           [java.util Locale]))

;; Apotheke Bank
(defn pdf-data [input-stream]
  (println input-stream)
  (with-open [pd (Loader/loadPDF input-stream)]
    (let [stripper (PDFTextStripper.)]
      (.getText stripper pd))))

(defn clean-str [s]
  (-> s
      (str/replace #"\u00A0" " ")
      (str/replace #"\s+" " ")
      (str/trim)))

(defn parse-amount-apotheke [amount]
  (-> amount
      (str/replace "." "")  ; Remove thousand separator
      (str/replace "," ".")  ; Convert decimal separator
      Double/parseDouble))

(defn starts-with-date? [line]
  (re-matches #"^\d{1,2}\. \w{3}\. \d{4} .*" (clean-str line)))

(defn merge-multi-line-transactions [lines]
  (loop [remaining lines
         current []
         grouped []]
    (if (empty? remaining)
      (if (empty? current) grouped (conj grouped (str/join " " current)))  ; Ensure last transaction is added
      (let [line (first remaining)
            rest-lines (rest remaining)]
        (if (starts-with-date? line)
          (recur rest-lines [line] (if (empty? current) grouped (conj grouped (str/join " " current))))
          (recur rest-lines (conj current line) grouped))))))

;; Processing dates
(def german-months
  {"Jan" "01", "Feb" "02", "März" "03", "Apr" "04", "Mai" "05", "Juni" "06"
   "Juli" "07", "Aug" "08", "Sep" "09", "Okt" "10", "Nov" "11", "Dez" "12"})

(defn parse-german-date [date-str]
  (let [[_ day month year] (re-matches #"(\d{1,2})\. (\w+)\. (\d{4})" date-str)
        month-num (get german-months month)]
    (if month-num
      (LocalDate/parse (str year "-" month-num "-" (format "%02d" (Integer/parseInt day)))
                       (DateTimeFormatter/ofPattern "yyyy-MM-dd"))
      (throw (Exception. (str "Invalid month: " month))))))

(defn parse-line-2 [line]
  (let [[_ date text amount _]
        (re-matches #"(\d{1,2}\. [A-ZÄÖÜa-zäöüß]+\. \d{4}) (.*?) (\d{1,3}(\.\d{3})*,\d{2})(.*)" (clean-str line))]
    (when (and date text amount)
      {:date (.toString (parse-german-date date))
       :text text
       :amount (parse-amount-apotheke amount)})))

(defn extract-transactions-apotheke [data]
  (->> (str/split-lines data)
       (map clean-str)
       (remove str/blank?)
       (merge-multi-line-transactions)
       (map parse-line-2)
       (remove nil?)))


(defn process [input-stream]
  (into [] (extract-transactions-apotheke (pdf-data input-stream))))