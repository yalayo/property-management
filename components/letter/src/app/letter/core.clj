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
      
      [:paragraph {:size 10} (str "Frau/Herrn " (:last-name tenant))]
      [:paragraph {:size 10} (:street tenant)]
      [:paragraph {:spacing-after 80} (:location tenant)]
      
      [:heading {:style {:size 14}} "Nebenkostenabrechnung 2023"]

      [:paragraph {:size 10 :align :left :spacing-before 25 :spacing-after 5} (str "Sehr geehrte(r) Herrn/Frau " (:last-name tenant) ",")]

      [:paragraph {:size 10 :align :left} "anbei erhalten Sie die Betriebskostenabrechnung für das Jahr 2022."]
      [:paragraph {:size 10 :align :left :spacing-after 10} "Sie schließt mit einer Gutschrift für den 2022 i. H. von € (total hier)."]

      [:paragraph {:size 10 :align :left :spacing-after 50} "Bei Rückfragen sind wir gerne behilflich."]

      [:paragraph {:size 10 :align :left} "Mit freundlichen Grüßen"]

      [:paragraph {:size 10 :align :left} "Christian Friese und Rosa Martinez"]
    
      [:pagebreak]
      ;; Details
      [:heading {:style {:size 14}} "Details"]
      table] output)
    (.toByteArray output)))




(comment
  (def data {:headers '("Abrechnungsposten" "Gesamtkosten" "Vert.Kst." "Schlüssel" "Anteilig" "365 Tagen" "152 Tagen"),
             :content
             [{:1 "Allgemeinstrom", :2 425.8, :3 100.0, :4 "Whfl.", :5 3.42, :6 14.56, :7 6.06}
              {:1 "Versicherung", :2 4125.35, :3 100.0, :4 "Whfl.", :5 3.42, :6 141.09, :7 58.75}
              {:1 "Grundsteuer", :2 4244.38, :3 100.0, :4 "Whfl.", :5 3.42, :6 145.16, :7 60.45}
              {:1 "Müllabfuhr", :2 2918.4, :3 100.0, :4 "Whfl.", :5 3.42, :6 99.81, :7 41.56}
              {:1 "Niederschlagwasser", :2 553.52, :3 100.0, :4 "Whfl.", :5 3.42, :6 18.93, :7 7.88}
              {:1 "Schmutzwasser", :2 3251.01, :3 100.0, :4 "Whfl.", :5 3.42, :6 111.18, :7 46.3}
              {:1 "Straßenreinigung", :2 241.86, :3 100.0, :4 "Whfl.", :5 3.42, :6 8.27, :7 3.44}
              {:1 "Trinkwasser", :2 1878.11, :3 100.0, :4 "Whfl.", :5 3.42, :6 64.23, :7 26.75}
              {:1 "Sondernutzungsgebühren", :2 33.6, :3 100.0, :4 "Whfl.", :5 3.42, :6 1.15, :7 0.48}
              {:1 "Rauchwarnmelder", :2 704.79, :3 100.0, :4 "Whfl.", :5 3.42, :6 24.1, :7 10.04}
              {:1 "Aufzugswartung", :2 4165.0, :3 100.0, :4 "Whfl.", :5 3.42, :6 142.44, :7 59.32}
              {:1 "Gebäudereinigung", :2 3960.0, :3 100.0, :4 "Whfl.", :5 3.42, :6 135.43, :7 56.4}
              {:1 "Aufzug Stromkosten", :2 621.53, :3 100.0, :4 "Whfl.", :5 3.42, :6 21.26, :7 8.85}
              {:1 "Telefonkosten", :2 312.49, :3 100.0, :4 "Whfl.", :5 3.42, :6 10.69, :7 4.45}]})

  ;; recreate the table
(let [table [:table {:spacing 5}]
      with-headers (conj table (into [] (map #(into [:cell {:align :center} %]) (:headers data)))) 
      result (into with-headers (map create-row (:content data)))]
  result))