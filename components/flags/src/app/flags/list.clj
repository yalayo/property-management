(ns app.flags.list
  (:require [app.flags.new-flag :as new-flag]))

(defn flag-info [flag]
  [:div {:class "lg:flex lg:items-center lg:justify-between gap-x-6 py-5 bg-white hover:bg-gray-100 p-4"}
   [:div
    {:class "min-w-0 flex-1"}
    [:h2
     {:class
      "text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight"}
     (:name flag)]]
   [:div {:class "mt-5 flex lg:ml-4 lg:mt-0"}
    [:button
     {:type "button",
      :class "inline-flex items-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
      :onclick (str "window.open('/flags/" (:id flag) "', '_blank');")}
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
     "Brief"]]])

(defn content [flags]
  [:main
   (new-flag/get-new-flag-form)
   [:button {:class "inline-block shrink-0 rounded-md border border-blue-600 bg-blue-600 px-12 py-3 text-sm font-medium text-white transition hover:bg-transparent hover:text-blue-600 focus:outline-none focus:ring active:text-blue-500 dark:hover:bg-blue-700 dark:hover:text-white"
             :_ "on click remove .translate-x-full from #slide-over-new-flag then remove .opacity-0 from #new-property"} "Hinzufügen"]
   [:div {:id "table" :class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 divide-y divide-gray-100"}
    (map flag-info flags)]])

