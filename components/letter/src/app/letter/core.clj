(ns app.letter.core
  (:require [clj-pdf.core :as pdf])
  (:import [java.io ByteArrayOutputStream]))

(defn create []
  (let [output (ByteArrayOutputStream.)]
    (pdf/pdf
     [{:title "Brief"
       :subject "Betriebskostenabrechnung"
       :author "Inmmo GmbH"
       :font {:family "Helvetica" :size 12}}
    
      ;; Title Section
      [:heading {:size 16} "Christian Friese & Rosa Martinez"]
      
      [:table {:widths [100] :border false}
       [[:cell
         [:paragraph "Herrn Mustermann"]
         [:paragraph "Bredowstr 2"]
         [:paragraph "45131 Essen"]]]]
      
      [:heading {:style {:size 14}} "Nebenkostenabrechnung 2023"]
    
      #_[:pagebreak]
      ;; Details
      [:heading {:style {:size 14}} "Details"]
      [:table {:spacing 5}
       ["Abrechnungsposten" "Gesamtkosten" "Vert.Kst." "Schlüssel" "Anteilig" "365 Tagen" "152 Tagen"]
       ["Allgemeinstrom" "425,8" "100" "Whfl." "3,42" "14,56 €" "6,06 €"]]] output)
    (.toByteArray output)))