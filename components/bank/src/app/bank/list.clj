(ns app.bank.list)

(defn any-missing-value? [m]
  (some nil? (vals m)))

(defn bank-info [account]
  [:div {:class "lg:flex lg:items-center lg:justify-between gap-x-6 py-5 bg-white hover:bg-gray-100 p-4"}
   [:div
    {:class "min-w-0 flex-1"}
    [:h2
     {:class
      "text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight"}
     (:name account)]]])

(defn content [accounts]
  [:main

   [:button {:class "inline-block shrink-0 rounded-md border border-blue-600 bg-blue-600 px-12 py-3 text-sm font-medium text-white transition hover:bg-transparent hover:text-blue-600 focus:outline-none focus:ring active:text-blue-500 dark:hover:bg-blue-700 dark:hover:text-white"
             :_ "on click remove .translate-x-full from #slide-over-new-property then remove .opacity-0 from #new-property"} "Hinzufügen"]
   [:div {:id "table" :class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 divide-y divide-gray-100"}
    (map bank-info accounts)]])



