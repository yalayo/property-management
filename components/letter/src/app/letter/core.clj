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
    
      ;; Tenant info
      [:table {:widths [100] :border false}
       ["Herrn"]
       ["Mustermann"]
       ["Bredowstr 2"]
       ["45131 Essen"]]
      
      [:table {:widths [100 100] :border false}
       [[:cell
         [:heading {:style {:size 15}} "Seller"]
         [:paragraph "CCS GmbH"]
         [:paragraph "Oberer Markt 9, DE 92507 Nabburg"]
         [:paragraph "Tax Number: -"]
         [:paragraph "VAT ID: DE111122223"]]
        [:cell
         [:heading {:style {:size 15}} "Buyer"]
         [:paragraph "Beispielmieter GmbH"]
         [:paragraph "Verwaltung Straße 40, DE 12345 Musterstadt"]]]]
    
      ;; Details
      [:heading {:style {:size 15}} "Invoice Items"]
      [:table {:widths [30 70 180 60 60] :spacing 5}
       ["Stk" "Art. Nummer" "Beschreibung" [:cell {:align :center} "Stückpreis"] [:cell {:align :center} "Betrag"]]]] output)
    (.toByteArray output)))