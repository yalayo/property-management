(ns app.flags.list)

(defn any-missing-value? [m]
  (some nil? (vals m)))

(defn flag-info [flag]
  [:div {:class "lg:flex lg:items-center lg:justify-between gap-x-6 py-5 bg-white hover:bg-gray-100 p-4"}
   [:div
    {:class "min-w-0 flex-1"}
    [:h2
     {:class
      "text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight"}
     (:last-name flag)]
    [:div
     {:class
      "mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6"}
     [:div
      {:class "mt-2 flex items-center text-sm text-gray-500"} 
      (:street flag)]]]
   [:div {:class "mt-5 flex lg:ml-4 lg:mt-0"}
    (if (any-missing-value? flag)
      [:div
       {:class "px-4 pb-4 pt-5 sm:p-6 sm:pb-4"}
       [:div
        {:class "sm:flex sm:items-start"}
        [:div
         {:class
          "mx-auto flex size-12 shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:size-10"}
         [:svg
          {:class "size-6 text-red-600",
           :fill "none",
           :viewBox "0 0 24 24",
           :stroke-width "1.5",
           :stroke "currentColor",
           :aria-hidden "true",
           :data-slot "icon"}
          [:path
           {:stroke-linecap "round",
            :stroke-linejoin "round",
            :d
            "M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"}]]]
        [:div {:class "mt-3 text-center sm:ml-4 sm:mt-0 sm:text-left"}
         [:div {:class "mt-2"}
          [:p {:class "text-sm text-gray-500"} "Fehlende Informationen. Bitte überprüfen Sie Ihre Excel-Datei."]]]]]
      [:button
       {:type "button",
        :class "inline-flex items-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
        :onclick (str "window.open('/flags/" (:flag-id flag) "', '_blank');")}
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
       "Brief"])]])

(defn content [flags]
  [:main
   [:div {:id "table" :class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 divide-y divide-gray-100"}
    (map flag-info flags)]])

