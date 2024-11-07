(ns app.letter.core
  (:require [clj-pdf.core :as pdf])
  (:import [java.io ByteArrayOutputStream]))

(defn create-row [data]
  (into [] (map (fn [item]
                  (if (= item :1)
                    (item data)
                    (into [] [:cell {:align :center} (item data)]))) (keys data))))

(defn create [headers content]
  (let [output (ByteArrayOutputStream.)
        scaffold [:table {:spacing 5 :font-size 8}]
        with-headers (into scaffold [(into [] headers)])
        table (into with-headers (map create-row (:content content)))]
    (pdf/pdf
     [{:title "Brief"
       :subject "Betriebskostenabrechnung"
       :author "Inmmo GmbH"
       :font {:family "Helvetica" :size 12}}
    
      ;; Title Section
      [:heading {:size 16} "Christian Friese & Rosa Martinez"]
      
      [:table {:widths [100] :border false :font-size 6}
       [[:cell
         [:paragraph "Herrn Mustermann"]
         [:paragraph "Bredowstr 2"]
         [:paragraph "45131 Essen"]]]]
      
      [:heading {:style {:size 14}} "Nebenkostenabrechnung 2023"]
    
      #_[:pagebreak]
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
      with-headers (into table [(into [] (:headers data))])
      result (into with-headers (map create-row (:content data)))]
  result)
  
  )