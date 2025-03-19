(ns app.html.dashboard)

(defn content []
  [:div
   {:class "bg-white py-24 sm:py-32"}
   [:div
    {:class "mx-auto max-w-7xl px-6 lg:px-8"}
    [:dl
     {:class
      "grid grid-cols-1 gap-x-8 gap-y-16 text-center lg:grid-cols-3"}
     [:div
      {:class "mx-auto flex max-w-xs flex-col gap-y-4"}
      [:dt
       {:class "text-base/7 text-gray-600"}
       "Transactions every 24 hours"]
      [:dd
       {:class
        "order-first text-3xl font-semibold tracking-tight text-gray-900 sm:text-5xl"}
       "44 million"]]
     [:div
      {:class "mx-auto flex max-w-xs flex-col gap-y-4"}
      [:dt {:class "text-base/7 text-gray-600"} "Assets under holding"]
      [:dd
       {:class
        "order-first text-3xl font-semibold tracking-tight text-gray-900 sm:text-5xl"}
       "$119 trillion"]]
     [:div
      {:class "mx-auto flex max-w-xs flex-col gap-y-4"}
      [:dt {:class "text-base/7 text-gray-600"} "New users annually"]
      [:dd
       {:class
        "order-first text-3xl font-semibold tracking-tight text-gray-900 sm:text-5xl"}
       "46,000"]]]]])