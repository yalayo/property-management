(ns app.bank.list)

(defn account-item [account]
  (let [account-id (:id account)
        input-id (str "file-upload-" account-id)
        content-id (str "content-" account-id)]
    [:div {:class "flex items-center p-2 border rounded-lg shadow"}
     [:svg {:class "h-7 w-7 mr-3"
            :viewBox "0 0 100 100"}
      [:circle {:cx "50", :cy "50", :r "45", :fill "#505050"}]
      [:path
       {:d
        "M 30 40 L 70 40 L 70 70 L 30 70 Z M 35 45 L 65 45 M 35 50 L 65 50 M 35 55 L 65 55 M 40 30 A 5 5 0 1 1 40 31 Z M 50 30 A 5 5 0 1 1 50 31 Z M 60 30 A 5 5 0 1 1 60 31 Z",
        :fill "none",
        :stroke "#FFFFFF",
        :stroke-width "2"}]]
     [:div {:class "flex-grow"}
      [:p
       {:class "text-md font-semibold"}
       (account :description)]]
     [:div {:id (:id account) :class "mt-4 flex items-center justify-center gap-x-6"}
      [:form
       {:hx-encoding "multipart/form-data"
        :hx-post "/upload-transactions"
        :hx-target (str "#" content-id)
        :hx-swap "innerHTML"}
       [:div
        [:input {:type "file"
                 :name "file"
                 :id input-id
                 :class "hidden"
                 :hx-on "change: this.form.requestSubmit();"}]
        [:label {:for input-id
                 :id (str "upload-label-" account-id)
                 :class "cursor-pointer inline-block shrink-0 rounded-md border border-blue-600 bg-blue-600 px-12 py-3 text-sm font-medium text-white transition hover:bg-transparent hover:text-blue-600 focus:outline-none focus:ring active:text-blue-500 dark:hover:bg-blue-700 dark:hover:text-white"}
         "Hochladen"]]]]
    
     [:a
      {:href "#",
       :class "text-gray-500 hover:text-gray-700 focus:outline-none"}
      [:svg {:class "h-6 w-6",
             :fill "none",
             :viewBox "0 0 24 24",
             :stroke "currentColor"}
       [:path
        {:stroke-linecap "round",
         :stroke-linejoin "round",
         :stroke-width "2",
         :d
         "M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"}]]]
     [:div {:id content-id}]]))

(defn content [accounts]
  [:div {:class "space-y-3"}
    (map account-item accounts)])



