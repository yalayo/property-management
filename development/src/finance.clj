(ns finance
  (:require [clojure.string :as str])
  (:import [org.apache.pdfbox Loader]
           [org.apache.pdfbox.text PDFTextStripper]
           [java.net URL]
           [java.io File]
           [java.lang Character]
           [java.time LocalDate]
           [java.time.format DateTimeFormatter]
           [java.util Locale]))

(defn text-of-pdf []
  (with-open [pd (Loader/loadPDF (new File "example.pdf"))]
    (let [stripper (PDFTextStripper.)]
      (.getText stripper pd))))

(text-of-pdf)

(def raw-data 
  [#_"https://my.hypovereinsbank.de/portal?view=/de/banking/konto/kontofuehrung/umsaetze.jsp"
   #_"06.11.2024 - 10:08 Uhr                                                                                                                                                                                                                                                    Seite 1"
   #_"Kontoinformationen"
   #_"Konto-Nr. 23681072 (HVB AktivKonto) BLZ 36020186"
   #_"IBAN DE10360201860023681072 Swift (BIC) HYVEDEMM360"
   #_"Aktueller Kontosaldo    10:03 MEZ 6. November 2024 1.243,89 EUR"
   #_"Im ausgewählten Zeitraum 01.06.2024 - 30.06.2024 liegen Umsätze vom 28.06.2024 - 03.06.2024 vor."
   #_"Kontostand am 30.06.2024 4.004,30 EUR"
   #_"Auswahlkriterien"
   #_"Datum: 01.06.2024 - 30.06.2024"
   #_"Umsätze im gewählten Zeitraum"
   #_"Buchung Valuta Verwendungszweck Betrag"
   "24.06.2024 24.06.2024 SEPA-GUTSCHRIFT"
   "ECHTZEITÜBERWEISUNG"
   "SIAMAK RAFI"
   "DE28300209005390662148"
   "DR. SIAMAK RAFI MITE LILIEN STR 9 JULI 2024"
   "AUFTRAG UM 06:04 UHR AUSGEF ÜHRT"
   "1.990,00 EUR"
   "07.06.2024 07.06.2024 SEPA-GUTSCHRIFT"
   "E.ON Energie Deutschland Gm bH"
   "Jahresabrechnung + offene P osten Nr. 99990300002283893"
   "0 zu Vertrag 403427253, Kun dennummer 203311556"
   "KUNDENREFERENZ A1.203311556 .403427253.185787134"
   "1.424,78 EUR"
   "28.06.2024 28.06.2024 SEPA-DAUERAUFTRAG"
   "Christa Kolb"
   "Miete + NK"
   "1.259,38 EUR"
   "26.06.2024 26.06.2024 SEPA-GUTSCHRIFT"
   "ECHTZEITÜBERWEISUNG"
   "S. Riemann"
   "DE72370190001010545004"
   "Miete und NK Juli 2024"
   "AUFTRAG UM 14:51 UHR AUSGEF ÜHRT"
   "1.180,00 EUR"
   "28.06.2024 28.06.2024 SEPA-GUTSCHRIFT"
   "Kübra Kececi"
   "Miete für Juli/Kececi"
   "1.085,00 EUR"
   "28.06.2024 28.06.2024 SEPA-GUTSCHRIFT"
   "Steden, Tanja"
   "Miete Juli Garage Lilienstr 9"
   "KUNDENREFERENZ 933103632BBB SLFZ"
   "90,00 EUR"
   "03.06.2024 03.06.2024 SEPA-DAUERAUFTRAG"
   "Michael Schafer Dominique S chafer"
   "Garagenmiete Fam. Schafer, Lilienstr.7, Essen"
   "75,00 EUR"])

(println (text-of-pdf))

(str/split (text-of-pdf) #"\r\n")

(defn get-report-date [text]
  (let [data (str/split text #"Uhr")
        date-time (first data)]
    (str/trim date-time)))

(defn get-account-number [text]
  (let [data (second (str/split text #"\."))
        result (first (str/split data #"\("))]
    (str/trim result)))

(defn get-iban [text]
  (str/trim (second (str/split text #" "))))

(let [data (str/split (text-of-pdf) #"\r\n")
      url (first data)
      report-date (get-report-date (second data))
      account-number (get-account-number (nth data 3))
      iban (get-iban (nth data 4))]
  iban)



(defn parse-amount [amount-str]
  (-> amount-str
      (clojure.string/replace #"[^\d,]" "") ;; Remove non-numeric characters except comma
      (clojure.string/replace #"," ".")    ;; Convert comma to dot
      Double/parseDouble))

(defn parse-transaction [[header details]]
  (let [[date1 date2 & _] (clojure.string/split (first header) #" ")
        amount (last details)
        details-str (clojure.string/join " " (butlast details))]
    {:date1 date1
     :date2 date2
     :details details-str
     :amount (parse-amount amount)}))

(defn extract-transactions [lines]
  (let [transaction-start #"^\d{2}\.\d{2}\.\d{4} \d{2}\.\d{2}\.\d{4} SEPA-.*"
        grouped (partition-by #(re-matches transaction-start %) lines)
        transactions (partition 2 grouped)]
    (map parse-transaction transactions)))

(extract-transactions raw-data)

(re-matches #"^\d{2}\.\d{2}\.\d{4} \d{2}\.\d{2}\.\d{4} SEPA-.*" "24.06.2024 24.06.2024 SEPA-GUTSCHRIFT")

(partition 2 (partition-by #(re-matches #"^\d{2}\.\d{2}\.\d{4} \d{2}\.\d{2}\.\d{4} SEPA-.*" %) raw-data))

(let [result (partition 2 (partition-by #(re-matches #"^\d{2}\.\d{2}\.\d{4} \d{2}\.\d{2}\.\d{4} SEPA-.*" %) raw-data))]
  (map parse-transaction result))

;; Apotheke Bank
(defn pdf-data []
  (with-open [pd (Loader/loadPDF (new File "example-2.pdf"))]
    (let [stripper (PDFTextStripper.)]
      (.getText stripper pd))))

(println (pdf-data))


(defn parse-amount-apotheke [amount]
  (-> amount
      (str/replace "." "")  ; Remove thousand separator
      (str/replace "," ".")  ; Convert decimal separator
      Double/parseDouble))   ; Convert to number

(defn parse-line [line]
  (when-some [[_ date name text amount] (re-matches #"(\d{1,2}\. \w{3}\. \d{4}) (.+?) (.+?); ([\d\.,]+)" line)]
    {:date date
     :name name
     :text text
     :amount (parse-amount-apotheke amount)}))

(defn clean-data [data]
  (-> data
      (str/replace #"\u00A0" " ")  ; Replace non-breaking spaces with normal spaces
      (str/trim)))

(defn extract-transactions-apotheke [data]
  (->> (str/split-lines data)
       (map clean-data)
       (remove str/blank?)
       (map parse-line)
       (remove nil?)))

;; Run extraction
(count (extract-transactions-apotheke (pdf-data)))

;; Another aproach
(defn clean-str [s]
  (-> s
      (str/replace #"\u00A0" " ")  ; Handle non-breaking spaces
      (str/replace #"\s+" " ")      ; Normalize multiple spaces
      (str/trim)))

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
      {:date (parse-german-date date)
       :text text
       :amount (parse-amount-apotheke amount)})))

(defn extract-transactions-apotheke-2 [data]
  (->> (str/split-lines data)
       (map clean-str)
       (remove str/blank?)
       (merge-multi-line-transactions)
       (map parse-line-2)
       (remove nil?)))

(extract-transactions-apotheke-2 (pdf-data))
(count (extract-transactions-apotheke-2 (pdf-data)))

(def example "1. Dez. 2023 NATASA GACESA Germany Überweisungsgutschrift; NATASA GACESA\r\n
                            Germany; MITTE;\r\n
                            935,00")
(parse-line-2 "1. Dez. 2023 NATASA GACESA Germany Überweisungsgutschrift; NATASA GACESA Germany; MITTE; 935,00")
(extract-transactions-apotheke-2 example)