(ns app.bank.account-details)

(defn content [id-account]
  [:div "Load content from database:" 
   [:p {:class "mb-4"} (str "Account details. ID: " id-account)]
   [:div {:id "file-upload-container"}
    [:input#upload_pdf {:type "file",
             :accept "application/pdf",
             :class "opacity-0 absolute"}];FIXME "hidden"
    [:button {:_ "on click trigger click on #upload_pdf",;TODO Revisar esto
             :class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded shadow-md focus:outline-none mb-4"}
     "Upload PDF"]]
   [:a {
        :href "/bank",
        :class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded shadow-md focus:outline-none"} 
    "Back"]])
