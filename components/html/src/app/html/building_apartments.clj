(ns app.html.building-apartments)

(def ^{:private true} apartments [{:id 1 :name "Apartamento Central"}
                 {:id 2 :name "Villa Vista"}
                 {:id 3 :name "Residencia Aurora"}])

(defn- construct-form-details [id name]
  [:div
   [:h2
    {:class "text-3xl font-bold tracking-tight text-gray-900"}
    (str "ID: " id)]
   [:p
    {:class "text-3xl font-bold tracking-tight text-gray-900"}
    (str "Name: " name)]])

(defn show-apartment-details [details]
  (let [[id name] details]
    (println "ID in method: " id)
    (println "Name in method: " name)
    (construct-form-details id name)))
 
(defn get-apartments-detail [building]
  (let [{:keys [id name]} building]
    (println (str "Params received: " building)) 
    [:div.container.mx-auto
     [:h3
      {:class "text-2xl font-bold tracking-tight text-gray-900"}
      (str name ": ID-" id)]
     [:ul
      {:class "space-y-4"}
      (for [{:keys [id name]} apartments]
        [:li
         {:key id, :class "border rounded-md p-4 shadow-sm"}
         [:form {:action "/dashboard" :method "post"}
          [:p
           {:class "text-xl font-bold tracking-tight text-gray-900"}
           (str "ID: " id)]
          [:p
           {:class "text-xl font-bold tracking-tight text-gray-900"}
           (str "Name: " name)]
          [:input {:type "hidden", :name "id",  :value id}]
          [:input {:type "hidden", :name "name", :value name}]
          [:button
           {:type "submit",
            :class "flex-none rounded-md bg-indigo-500 px-3.5 py-2.5 text-sm font-semibold text-black shadow-sm hover:bg-indigo-400 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"}
           "Show Details"]]])]]))