(ns app.bank.list)

(defn account-item [account]
  [:div {:class "flex items-center p-2 border rounded-lg shadow"}
   [:svg
    {:class "h-7 w-7 mr-3"
     :viewBox "0 0 100 100"}
    [:circle {:cx "50", :cy "50", :r "45", :fill "#505050"}]
    [:path
     {:d
      "M 30 40 L 70 40 L 70 70 L 30 70 Z M 35 45 L 65 45 M 35 50 L 65 50 M 35 55 L 65 55 M 40 30 A 5 5 0 1 1 40 31 Z M 50 30 A 5 5 0 1 1 50 31 Z M 60 30 A 5 5 0 1 1 60 31 Z",
      :fill "none",
      :stroke "#FFFFFF",
      :stroke-width "2"}]]
   [:div
    {:class "flex-grow"}
    [:p
     {:class "text-md font-semibold"}
     (account :description)]]
   [:a
    {:href "#",
     :class "text-gray-500 hover:text-gray-700 focus:outline-none"}
    [:svg
     {:class "h-6 w-6",
      :fill "none",
      :viewBox "0 0 24 24",
      :stroke "currentColor"}
     [:path
      {:stroke-linecap "round",
       :stroke-linejoin "round",
       :stroke-width "2",
       :d
       "M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"}]]]
   ])

(defn content [accounts]
  [:div {:class "space-y-4"}
    (map account-item accounts)])



