(ns app.flags.new-flag)

(defn get-new-flag-form []
  [:form#add-flag
   {:class "relative z-10 pointer-events-none",
    :aria-labelledby "slide-over-title",
    :role "dialog",
    :aria-modal "true",
    :hx-post "/new-flag",
    :hx-target "this",
    :hx-swap "innerHTML"}
   [:div#new-flag
    {:class "fixed inset-0 bg-gray-500/75 transition-opacity ease-in-out duration-500 opacity-0",
     :aria-hidden "true"}]
   [:div
    {:class "fixed inset-0 overflow-hidden"}
    [:div
     {:class "absolute inset-0 overflow-hidden"}
     [:div
      {:class
       "pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10"}
      (comment
        "Slide-over panel, show/hide based on slide-over state.\n\n  Entering: \"transform transition ease-in-out duration-500 sm:duration-700\"\n            From: \"translate-x-full\"\n            To: \"translate-x-0\"\n          Leaving: \"transform transition ease-in-out duration-500 sm:duration-700\"\n            From: \"translate-x-0\"\n            To: \"translate-x-full\"")
      [:div#slide-over-new-flag
       {:class "pointer-events-auto w-screen max-w-md transform transition ease-in-out duration-500 sm:duration-700 translate-x-full"}
       [:div
        {:class
         "flex h-full flex-col overflow-y-scroll bg-white shadow-xl"} 
        [:div
         {:class "flex-1 overflow-y-auto px-4 py-6 sm:px-6"}
         [:div
          {:class "flex items-start justify-between"}
          [:h2
           {:class "text-lg font-medium text-gray-900",
            :id "slide-over-title"}
           "Neu Feature Flag hinzufügen!"]
          [:div
           {:class "ml-3 flex h-7 items-center"}
           [:button#close-panel
            {:class "relative -m-2 p-2 text-gray-400 hover:text-gray-500"}
            [:span {:class "absolute -inset-0.5"}]
            [:span {:class "sr-only"} "Close panel"]
            [:svg
             {:class "w-6 h-6",
              :fill "none",
              :viewBox "0 0 24 24",
              :stroke-width "1.5",
              :stroke "currentColor",
              :aria-hidden "true",
              :data-slot "icon"}
             [:path
              {:stroke-linecap "round",
               :stroke-linejoin "round",
               :d "M6 18 18 6M6 6l12 12"}]]]]]
         [:div
          {:class "mt-8"}
          [:div#form-new-ft  
          [:div
           {:class "flow-root"}
            [:div
             {:class "mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6"}
             [:div
              {:class "sm:col-span-6"}
              [:div {:class "mt-2"}
               [:div
                {:class
                 "flex items-center rounded-md bg-white pl-3 border border-gray-300 focus-within:border-2 focus-within:border-indigo-600"}
                [:div
                 {:class "shrink-0 text-base text-gray-500 select-none sm:text-sm/6"}
                 "Name: "]
                [:input
                 {:type "text",
                  :name "name",
                  :id "name",
                  :class
                  "w-full py-2 text-base text-gray-900 placeholder:text-gray-400 focus:outline-none sm:text-sm/6",
                  :placeholder ""}]]]]]]]]]
        [:div {:class "border-t border-gray-200 px-4 py-6 sm:px-6"}
         [:div
          {:class "mt-6"}
          [:button {:class "flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-6 py-3 text-base font-medium text-white shadow-xs hover:bg-indigo-700"}
           "Hinzufügen"]]]]]]]]])