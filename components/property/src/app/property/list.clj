(ns app.property.list
  (:require [app.property.new-property :as new-property]))

(defn any-missing-value? [m]
  (some nil? (vals m)))

(defn property-info [property]
  [:div {:class "lg:flex lg:items-center lg:justify-between gap-x-6 py-5 bg-white hover:bg-gray-100 p-4"}
   [:div
    {:class "min-w-0 flex-1"}
    [:h2
     {:class
      "text-2xl font-bold leading-7 text-gray-900 sm:truncate sm:text-3xl sm:tracking-tight"}
     (:name property)]
    [:div
     {:class
      "mt-1 flex flex-col sm:mt-0 sm:flex-row sm:flex-wrap sm:space-x-6"}
     [:div
      {:class "mt-2 flex items-center text-sm text-gray-500"} 
      (:street property)]]]
   [:div {:class "mt-5 flex lg:ml-4 lg:mt-0"}
    (if (any-missing-value? property)
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
      (let [property-id (:id property)
            input-id (str "file-upload-" property-id)
            content-id (str "content-" property-id)]
        [:div {:id (:id property) :class "flex items-center space-x-2"}
         [:form
          {:hx-encoding "multipart/form-data"
           :hx-post "/upload-property-details"
           :hx-target (str "#" content-id)
           :hx-swap "innerHTML"}
          [:div
           [:input {:type "file"
                    :name "file"
                    :id input-id
                    :accept ".xls,.xlsx"
                    :class "hidden"
                    :hx-on "change: this.form.requestSubmit();"}]
           [:label {:for input-id
                    :id (str "upload-label-" property-id)
                    :class "cursor-pointer inline-block shrink-0 rounded-md border border-gray-600 bg-gray-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-transparent hover:text-gray-600 focus:outline-none focus:ring active:text-gray-500 dark:hover:bg-gray-700 dark:hover:text-white"}
            "Hochladen"]]]]) 
      #_[:a
       {:class "inline-flex items-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
        :href (str "/property/" (:name property))} 
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

(defn content [properties]
  [:main
   (new-property/get-new-property-form)
   [:button {:class "inline-block shrink-0 rounded-md border border-blue-600 bg-blue-600 px-12 py-3 text-sm font-medium text-white transition hover:bg-transparent hover:text-blue-600 focus:outline-none focus:ring active:text-blue-500 dark:hover:bg-blue-700 dark:hover:text-white"
             :_ "on click remove .translate-x-full from #slide-over-new-property then remove .opacity-0 from #new-property"} "Hinzufügen"]
   [:div {:id "table" :class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 divide-y divide-gray-100"}
    (map property-info properties)]])



(defn get-property-details [name]
   [:div
    {:class "bg-white"}
    [:div
     {:class "relative isolate px-6 pt-14 lg:px-8"}
     [:div
      {:class "mx-auto max-w-2xl"}
      [:div
       {:class "text-center"}
        [:h3
        {:class "text-base/7 font-semibold text-gray-900"}
        "Property name:" " " name]
       [:h1 {:class "text-balance text-4xl font-bold tracking-tight text-gray-900 sm:text-6xl"} "Kalkulationen importieren"]
       [:div {:id "upload-file" :class "mt-10 flex items-center justify-center gap-x-6"}
        [:form
         {:hx-encoding "multipart/form-data"
          :hx-post "/upload-property-details"
          :hx-target "this"
          :hx-swap "#upload-file"}
         [:div
          [:input {:type "file"
                   :name "file"
                   :id "property-bank-data"
                   :class "hidden"
                   :hx-on "change: this.form.requestSubmit();"}]
          [:label {:for "property-bank-data"
                   :id "upload-label-property"
                   :class "cursor-pointer inline-block shrink-0 rounded-md border border-blue-600 bg-blue-600 px-12 py-3 text-sm font-medium text-white transition hover:bg-transparent hover:text-blue-600 focus:outline-none focus:ring active:text-blue-500 dark:hover:bg-blue-700 dark:hover:text-white"}
           "Hochladen"]]]]]]
     [:div
      {:class
       "absolute inset-x-0 top-[calc(100%-13rem)] -z-10 transform-gpu overflow-hidden blur-3xl sm:top-[calc(100%-30rem)]",
       :aria-hidden "true"}
      [:div
       {:class
        "relative left-[calc(50%+3rem)] aspect-[1155/678] w-[36.125rem] -translate-x-1/2 bg-gradient-to-tr from-[#ff80b5] to-[#9089fc] opacity-30 sm:left-[calc(50%+36rem)] sm:w-[72.1875rem]",
        :style
        "clip-path: polygon(74.1% 44.1%, 100% 61.6%, 97.5% 26.9%, 85.5% 0.1%, 80.7% 2%, 72.5% 32.5%, 60.2% 62.4%, 52.4% 68.1%, 47.5% 58.3%, 45.2% 34.5%, 27.5% 76.7%, 0.1% 64.9%, 17.9% 100%, 27.6% 76.8%, 76.1% 97.7%, 74.1% 44.1%)"}]]]])