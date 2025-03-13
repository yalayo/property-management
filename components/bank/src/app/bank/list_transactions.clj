(ns app.bank.list-transactions)

(defn transaction-item [transaction]
  [:div {:class "lg:flex lg:items-center lg:justify-between gap-x-6 py-5 bg-white hover:bg-gray-100 p-4"}
   [:div
    {:class "min-w-0 flex-1"}
    [:span {:class "text-sm text-gray-600 font-medium"} (str (:date transaction))]
    [:p {:class "text-md font-semibold mt-1"} (:text transaction)]
    [:span {:class "text-lg font-bold mt-1"} (str (:amount transaction) " EUR")]]
   [:div {:class "mt-5 flex lg:ml-4 lg:mt-0"}
    [:button
     {:type "button",
      :class "inline-flex items-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
      :onclick (str "window.open('/transactions/" (:id transaction))}
     [:svg
      {:class "-ml-0.5 mr-1.5 h-5 w-5",
       :viewBox "0 0 20 20",
       :fill "currentColor",
       :aria-hidden "true"}
      [:path
       {:fill-rule "evenodd",
        :d
        "M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z",
        :clip-rule "evenodd"}]]
     "Speichern"]]])

(defn content [transactions]
  (for [transaction transactions]
    (transaction-item transaction)))