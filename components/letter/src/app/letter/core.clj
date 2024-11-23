(ns app.letter.core
  (:require [clj-pdf.core :as pdf])
  (:import [java.io ByteArrayOutputStream]
           [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

(defn create-headers [data]
  (into [] (map (fn [item]
                  (let [element (item data)]
                    (case item
                      :1 (into [] [:pdf-cell {:align :left :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9} element]])
                      (into [] [:pdf-cell {:align :right :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9} element]])))) (keys data))))

(defn get-rows-but-last-three [rows]
  (let [n (count rows)
        split-point (max 0 (- n 3))]
    (into [](take split-point rows))))

(defn get-rows-last-three [rows]
  (into [] (take-last 3 rows)))

(defn create-row [data]
  (into [] (map (fn [item]
                  (let [element (item data)]
                    (case item
                      :1 (into [] [:pdf-cell {:valign :middle :border true} [:paragraph {:size 9} element]])
                      :3 (into [] [:pdf-cell {:align :right :valign :middle :border true} [:paragraph {:size 9} (if (and (some? element) (= element (Math/floor element))) (int (Math/floor element)) element)]])
                      :5 (into [] [:pdf-cell {:align :right :valign :middle :border true} [:paragraph {:size 9} (if (and (some? element) (= element (Math/floor element))) (int (Math/floor element)) element)]])
                      (if (float? (item data))
                        (into [] [:pdf-cell {:align :right :valign :middle :border true} [:paragraph {:size 9} (str (format "%.2f" element) " €")]])
                        (into [] [:pdf-cell {:align :right :valign :middle :border true} [:paragraph {:size 9} element]]))))) (keys data))))

(defn create-last-three-rows [data]
  (into [] (map (fn [item]
                  (let [element (item data)]
                    (case item
                      :1 (into [] [:pdf-cell {:valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9 :style :bold} element]])
                      :3 (into [] [:pdf-cell {:align :right :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9 :style :bold} (if (and (some? element) (= element (Math/floor element))) (int (Math/floor element)) element)]])
                      :5 (into [] [:pdf-cell {:align :right :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9 :style :bold} (if (and (some? element) (= element (Math/floor element))) (int (Math/floor element)) element)]])
                      (if (float? (item data))
                        (into [] [:pdf-cell {:align :right :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9 :style :bold} (str (format "%.2f" element) " €")]])
                        (into [] [:pdf-cell {:align :right :valign :middle :border true :background-color [189 215 238]} [:paragraph {:size 9 :style :bold} element]]))))) (keys data))))

(defn payment-information [total payment-info]
  [:pdf-table
   {:width-percent 100 :cell-border false :spacing-before 10
    :header [[[:pdf-cell {:colspan 2 :padding-bottom 20} [:paragraph {:size 10 :align :left} "Aus der Abrechnung und  unter Berücksichtigung Ihrer Vorauszahlungen ergibt sich eine "[:phrase {:style :bold} "Nachzahlung"] " in Höhe von " [:phrase {:style :bold} (str (format "%.2f" total) " €")]]]]]}
   [14 86]
   [[:pdf-cell {:colspan 2 :padding-bottom 20} [:paragraph {:size 10 :align :left} "Wir bitten um Ausgleich unter Angabe Ihrer Wohnungsnummer binnen 14 Tagen auf folgendes Bankkonto:"]]]
   [[:pdf-cell {:valign :middle} [:paragraph {:size 10} "IBAN:"]] [:pdf-cell {:valign :middle} [:paragraph {:size 10 :style :bold} (:iban payment-info)]]]
   [[:pdf-cell {:valign :middle} [:paragraph {:size 10} "BANK:"]] [:pdf-cell {:valign :middle} [:paragraph {:size 10 :style :bold} (:bank-name payment-info)]]]])

(defn create [tenant]
  (let [output (ByteArrayOutputStream.)
        today (.format (LocalDate/now) (DateTimeFormatter/ofPattern "dd.MM.yyyy"))
        headers (:headers tenant)
        content (:content tenant)
        first-rows (get-rows-but-last-three content)
        last-rows (get-rows-last-three content)
        scaffold [:pdf-table {:width-percent 100 :cell-border true} (into [20 20] (repeatedly (- (count headers) 2) #(/ 60 (- (count headers) 2))))]
        with-headers (conj scaffold (create-headers headers))
        first-part (into with-headers (map create-row first-rows))
        table (into first-part (map create-last-three-rows last-rows))]
    (println "Tenant: " tenant)
    (pdf/pdf
     [{:title "Brief"
       :subject "Betriebskostenabrechnung"
       :author "Inmmo GmbH"
       :font {:family "Helvetica" :size 6}
       :left-margin   60
       :right-margin  40
       :footer {:text (get-in tenant [:property-info :id]) :page-numbers false :align :right}}

      [:paragraph {:size 8 :align :left :spacing-after 20} "Christian Friese & Rosa Martinez - Reckmannshof 10 45133 Essen"] 

      [:paragraph {:size 10 :align :right :spacing-after 80} (str "Essen, " today)]

      [:pdf-table
       {:width-percent 100}
       [50 50]
       [[:pdf-cell {:border false} [:paragraph {:size 10} (:last-name tenant)]
         [:paragraph {:size 10} (:street tenant)]
         [:paragraph {:size 10 :spacing-after 20} (:location tenant)]]
        [:pdf-cell 
         [:pdf-table
          {:width-percent 100 :cell-border false}
          [50 50]
          [[:pdf-cell {:valign :middle :background-color [189 215 238]} [:paragraph {:size 9} (get-in tenant [:property-info :name])]] [:pdf-cell {:valign :middle} [:paragraph {:size 9} (get-in tenant [:property-info :address])]]]
          [[:pdf-cell {:valign :middle :background-color [189 215 238]} [:paragraph {:size 9} "Wohnung"]] [:pdf-cell {:valign :middle} [:paragraph {:size 9} (get-in tenant [:property-info :apartment])]]]
          [[:pdf-cell {:valign :middle :background-color [189 215 238]} [:paragraph {:size 9} "Zeitraum"]] [:pdf-cell {:valign :middle} [:paragraph {:size 9} (get-in tenant [:property-info :time-period])]]]
          [[:pdf-cell {:valign :middle :background-color [189 215 238]} [:paragraph {:size 9} "Abrechnungstage"]] [:pdf-cell {:valign :middle} [:paragraph {:size 9} (int (Math/floor (get-in tenant [:property-info :calculated-days])))]]]
          (when (some? (get-in tenant [:property-info :days-per-person]))
            [[:pdf-cell {:valign :middle :background-color [189 215 238]} [:paragraph {:size 9} "Abrechnungstage*Pers"]] [:pdf-cell {:valign :middle} [:paragraph {:size 9} (int (Math/floor (get-in tenant [:property-info :days-per-person])))]]])]]]] 

      [:paragraph {:size 10 :align :left :spacing-before 30 :spacing-after 5} (str "Sehr geehrte " (:last-name tenant) ",")]

      [:paragraph {:size 10 :align :left :spacing-after 10} "mit diesem Schreiben erhalten Sie gemäß §556 BGB Abs. 3 die Abrechnung der Betriebskosten für das Jahr 2023."] 

      table

      (if (:refund tenant)
        [:paragraph {:size 10 :align :left :spacing-before 10} "Sie schließt mit einer Gutschrift für den 2023 i. H. von " [:phrase {:style :bold} (str (format "%.2f" (:total tenant)) " €")]]
        (payment-information (:total tenant) (:payment-info tenant)))

      [:paragraph {:size 10 :align :left :spacing-before 10 :spacing-after 10} "Bei Rückfragen sind wir gerne behilflich."]

      [:paragraph {:size 10 :align :left :spacing-before 20 :spacing-after 30} "Mit freundlichen Grüßen"]

      [:paragraph {:size 10 :align :left} "Christian Friese und Rosa Martinez"]] 
      output)
    (.toByteArray output)))




(comment
  (def data {:tenant-id "302d3365-e7fe-4875-b1f0-8b4ba52d6281",
             :last-name "Brusberg",
             :street "Kunigundastr.20",
             :location "45131 Essen",
             :total-costs 123.4
             :prepayment 100.35
             :heating-costs 13.0
             :total 10.4
             :refund false
             :headers '("Abrechnungsposten" "Gesamtkosten" "Vert.Kst" "Schlüssel" "Anteilig" "Ihr Anteil"),
             :content
             [{:1 "Allgemeinstrom ", :2 286.73, :3 100.0, :4 "Whfl.", :5 4.1, :6 11.75593}
              {:1 "Versicherung", :2 4125.35, :3 100.0, :4 "Whfl.", :5 4.1, :6 169.13935}
              {:1 "Grundsteuer", :2 4244.38, :3 100.0, :4 "Whfl.", :5 4.1, :6 174.01958}
              {:1 "Müllabfuhr", :2 2899.2, :3 100.0, :4 "Whfl.", :5 4.1, :6 118.86719999999998}
              {:1 "Niederschlagwasser", :2 544.64, :3 100.0, :4 "Whfl.", :5 4.1, :6 22.330239999999996}
              {:1 "Schmutzwasser", :2 3667.98, :3 100.0, :4 "Whfl.", :5 4.1, :6 150.38718}
              {:1 "Straßenreinigung", :2 250.27, :3 100.0, :4 "Whfl.", :5 4.1, :6 10.261069999999998}
              {:1 "Trinkwasser", :2 1950.0, :3 100.0, :4 "Whfl.", :5 4.1, :6 79.94999999999999}
              {:1 "Sondernutzungsebühren", :2 33.6, :3 100.0, :4 "Whfl.", :5 4.1, :6 1.3776}
              {:1 "Rauchwarnmelder", :2 328.54, :3 100.0, :4 "Whfl.", :5 4.1, :6 13.470139999999999}
              {:1 "Aufzugswartung", :2 4165.0, :3 100.0, :4 "Whfl.", :5 4.1, :6 170.765}
              {:1 "Gebäudereinigung", :2 3960.0, :3 100.0, :4 "Whfl.", :5 4.1, :6 162.35999999999999}
              {:1 "Aufzug Stromkosten ", :2 0.0, :3 100.0, :4 "Whfl.", :5 4.1, :6 0.0}
              {:1 "Telefonkosten", :2 228.92, :3 100.0, :4 "Whfl.", :5 4.1, :6 9.385719999999997}]})

  ;; recreate the table
  (let [table [:pdf-table {:cell-border true} nil]
        with-headers (conj table (into [] (map #(into [:pdf-cell {:align :left} %]) (:headers data))))
        result (into with-headers (map create-row (:content data)))]
    result)

  (defn byte-array-to-file
    [byte-array file-path]
    (with-open [out (java.io.FileOutputStream. file-path)]
      (.write out byte-array)))

  (byte-array-to-file (create data) "components/excel/resources/letter.pdf")
   
  (get-rows-but-last-three (:content data))
  (get-rows-last-three (:content data))
  )