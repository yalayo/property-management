(ns app.bank.list)

(defn account-item [account]
  (let [account-id (:id account)
        input-id (str "file-upload-" account-id)
        content-id (str "content-" account-id)]
    [:div
     [:div {:class "flex flex-col"}
     ;; Account Header Section (Icon + Description)
     [:div {:class "flex items-center border-b"}
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
       [:p {:class "text-md font-semibold"} (account :description)]]
      ;; Upload & More Options Section
     [:div {:id (:id account) :class "flex items-center space-x-2"}
      [:form
       {:hx-encoding "multipart/form-data"
        :hx-post "/upload-transactions"
        :hx-target (str "#" content-id)
        :hx-swap "innerHTML"}
       [:div
        [:input {:type "file"
                 :name "file"
                 :id input-id
                 :accept "application/pdf",
                 :class "hidden"
                 :hx-on "change: this.form.requestSubmit();"}]
        [:label {:for input-id
                 :id (str "upload-label-" account-id)
                 :class "cursor-pointer inline-block shrink-0 rounded-md border border-gray-600 bg-gray-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-transparent hover:text-gray-600 focus:outline-none focus:ring active:text-gray-500 dark:hover:bg-gray-700 dark:hover:text-white"}
         "Hochladen"]]]]
      ;; More Options Button
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
          "M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"}]]]]]

     ;; Content Section (Now Appears Below)
     [:div {:id content-id :class "mt-2"}]]))

(defn content [accounts]
  [:div {:class "space-y-3"}
    (map account-item accounts)])



