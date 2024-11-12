(ns app.letter.core
  (:require [clj-pdf.core :as pdf])
  (:import [java.io ByteArrayOutputStream]
           [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

(defn create-row [data]
  (into [] (map (fn [item]
                  (if (= item :1)
                    (into [] [:cell {:border false} (item data)])
                    (let [element (item data)]
                      (if (float? element)
                        (into [] [:cell {:align :center :border false} (str (format "%.2f" element) " €")])
                        (into [] [:cell {:align :center :border false} (item data)]))))) (keys data))))

(defn create [tenant]
  (let [output (ByteArrayOutputStream.)
        today (.format (LocalDate/now) (DateTimeFormatter/ofPattern "dd.MM.yyyy"))
        scaffold [:table {:spacing 0 :padding 2 :font-size 8}]
        headers (:headers tenant)
        content (:content tenant)
        with-headers (conj scaffold (into [] (map #(into [:cell {:align :center :border false :background-color [189 215 238]} %]) headers)))
        table (into with-headers (map create-row content))]
    (pdf/pdf
     [{:title "Brief"
       :subject "Betriebskostenabrechnung"
       :author "Inmmo GmbH"
       :font {:family "Helvetica" :size 6}}

      ;; Title Section
      [:heading {:size 16} "Christian Friese & Rosa Martinez"]

      [:paragraph {:size 10 :align :right :spacing-after 10} (str "Essen, " today)]

      [:paragraph {:size 10} (:last-name tenant)]
      [:paragraph {:size 10} (:street tenant)]
      [:paragraph {:size 10 :spacing-after 80} (:location tenant)]

      [:heading {:style {:size 14}} "Nebenkostenabrechnung 2023"]

      [:paragraph {:size 10 :align :left :spacing-before 25 :spacing-after 5} (str "Sehr geehrte(r) Herrn/Frau " (:last-name tenant) ",")]

      [:paragraph {:size 10 :align :left :spacing-after 20} "anbei erhalten Sie die Betriebskostenabrechnung für das Jahr 2023."]

      [:pdf-table
       {:width-percent 50 :cell-border false}
       [70 30]
       [[:pdf-cell {:valign :middle} [:paragraph {:size 10} "Kostenabrechnung"]] [:pdf-cell {:valign :middle} [:paragraph {:size 10 :style :bold} (str (format "%.2f" (:total-costs tenant)) " €")]]]
       [[:pdf-cell {:valign :middle} [:paragraph {:size 10} "Vorauszahlung Warm und Kalt"]] [:pdf-cell {:valign :middle} [:paragraph {:size 10 :style :bold} (str (format "%.2f" (:prepayment tenant)) " €")]]]
       [[:pdf-cell {:valign :middle} [:paragraph {:size 10} "Heißkosten"]] [:pdf-cell {:valign :middle} [:paragraph {:size 10 :style :bold} (str (format "%.2f" (:heating-costs tenant)) " €")]]]]

      [:paragraph {:size 10 :align :left :spacing-before 20 :spacing-after 10} (str "Sie schließt mit einer Gutschrift für den 2023 i. H. von " (format "%.2f" (:total tenant)) " €")]

      [:paragraph {:size 10 :align :left :spacing-after 50} "Bei Rückfragen sind wir gerne behilflich."]

      [:paragraph {:size 10 :align :left} "Mit freundlichen Grüßen"]

      [:paragraph {:size 10 :align :left} "Christian Friese und Rosa Martinez"]

      [:pagebreak]
      ;; Details
      [:heading {:style {:size 14}} "Details"]
      table] output)
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
  (let [table [:table {:spacing 5}]
        with-headers (conj table (into [] (map #(into [:cell {:align :center} %]) (:headers data))))
        result (into with-headers (map create-row (:content data)))]
    result)

  (defn byte-array-to-file
    [byte-array file-path]
    (with-open [out (java.io.FileOutputStream. file-path)]
      (.write out byte-array)))

  (byte-array-to-file (create data) "components/excel/resources/letter.pdf")
  )