(ns app.bank.account-details)

(defn content [id-account]
  [:div "Load content from database:" 
   [:p {:class "mb-4"} (str "Account details. ID: " id-account)]
   [:a {
        :href "/bank",
        :class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded shadow-md focus:outline-none"} 
    "Back"]])
