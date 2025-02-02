(ns app.html.user-buildings)

(def ^{:private true} data  {:cur-user "Current user"
            :buildings [{:id "0001" :name "Building 1" :address "Address 1" :pic "def-building.jpg"}
                        {:id "0002" :name "Building 2" :address "Address 2" :pic "def-building.jpg"}
                        {:id "0003" :name "Building 3" :address "Address 3" :pic "def-building.jpg"}]})

(defn get-buildings []
  [:div.container.mx-auto
   [:h1 {:class "text-2xl font-semibold text-gray-900"} (str (:cur-user data) " s'Buildings:")]
   [:ul
    {:role "list", :class "divide-y divide-gray-100"}
    (for [building (:buildings data)]
      [:li 
       {:key (:id building),
        :class "flex justify-between gap-x-6 py-5"}
       [:div
        {:class "flex min-w-0 gap-x-4"}
        [:img
         {:class "w-24 h-24 flex-none rounded-3xl bg-gray-50",
          :src (:pic building),
          :alt "Picture of building"}]
        [:div
         {:class "min-w-0 flex-auto"}
         [:form {:action "/building-apartments" :method "post"}
          [:input {:type "hidden", :name "id",  :value (:id building)}]
          [:input {:type "hidden", :name "name", :value (:name building)}]
          [:button
           {:type "submit",
            :class
            "border-none bg-transparent p-2 rounded text-blue-500 hover:text-blue-700 cursor-pointer transition duration-300"}
            (:name building)]]
         [:p
          {:class "mt-1 truncate text-xs/5 text-gray-500"}
          (:address building)]]]
       [:div
        {:class "hidden shrink-0 sm:flex sm:flex-col sm:items-end"}
        [:p {:class "text-sm/6 text-gray-900"}
         "Other data here!"]
        [:p
         {:class "mt-1 text-xs/5 text-gray-500"}
         "Other data here!"]
        ]])]])