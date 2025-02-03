(ns app.html.apartment-details)

(defn get-apartment-details [args]
  (let [{:keys [id name]} args]
  [:div 
   [:h4 (str "ID: " id " / Apartment: " name)]
   ]))