(ns app.html.upload-details)

(defn no-file-selected []
  [:div
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
       [:p {:class "text-sm text-gray-500"} "Fehlende Informationen. Stellen Sie sicher, dass eine Datei ausgewählt wurde."]]]]]
  [:div {:id "upload-file" :class "mt-10 flex items-center justify-center gap-x-6"}
   [:form
    {:hx-encoding "multipart/form-data"
     :hx-post "/upload-details"
     :hx-target "this"
     :hx-swap "#upload-file"}
    [:input {:type "file", :name "file"}]
    [:button.inline-block.shrink-0.rounded-md.border.border-blue-600.bg-blue-600.px-12.py-3.text-sm.font-medium.text-white.transition.hover:bg-transparent.hover:text-blue-600.focus:outline-none.focus:ring.active:text-blue-500.dark:hover:bg-blue-700.dark:hover:text-white "Hochladen"]]]])

(defn wrong-file-selected [errors]
  [:div
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
       [:p {:class "text-sm text-gray-500"} "Your Excel contains errors in the following cells: "]]
      [:ul {:class "mt-2 text-sm text-red-600"}
      ;; Aquí estamos iterando sobre los errores
       (for [{:keys [cell-address message sheet-name]} errors]
         [:li (str "Error in cell and sheet: " cell-address "and sheet: " sheet-name " : " message)])]]]]
   [:div {:id "upload-file" :class "mt-10 flex items-center justify-center gap-x-6"}
    [:form
     {:hx-encoding "multipart/form-data"
      :hx-post "/upload-details"
      :hx-target "this"
      :hx-swap "#upload-file"}
     [:input {:type "file", :name "file"}]
     [:button.inline-block.shrink-0.rounded-md.border.border-blue-600.bg-blue-600.px-12.py-3.text-sm.font-medium.text-white.transition.hover:bg-transparent.hover:text-blue-600.focus:outline-none.focus:ring.active:text-blue-500.dark:hover:bg-blue-700.dark:hover:text-white "Hochladen"]]]])

(defn page []
  [:div
   {:class "bg-white"}
   [:div
    {:class "relative isolate px-6 pt-14 lg:px-8"}
    [:div
     {:class "mx-auto max-w-2xl"}
     [:div
      {:class "text-center"}
      [:h1 {:class "text-balance text-4xl font-bold tracking-tight text-gray-900 sm:text-6xl"} "Kalkulationen importieren"]
      [:div {:id "upload-file" :class "mt-10 flex items-center justify-center gap-x-6"}
       [:form
        {:hx-encoding "multipart/form-data"
         :hx-post "/upload-details"
         :hx-target "this"
         :hx-swap "#upload-file"}
        [:input {:type "file", :name "file"}]
        [:button.inline-block.shrink-0.rounded-md.border.border-blue-600.bg-blue-600.px-12.py-3.text-sm.font-medium.text-white.transition.hover:bg-transparent.hover:text-blue-600.focus:outline-none.focus:ring.active:text-blue-500.dark:hover:bg-blue-700.dark:hover:text-white "Hochladen"]]]]]
    [:div
     {:class
      "absolute inset-x-0 top-[calc(100%-13rem)] -z-10 transform-gpu overflow-hidden blur-3xl sm:top-[calc(100%-30rem)]",
      :aria-hidden "true"}
     [:div
      {:class
       "relative left-[calc(50%+3rem)] aspect-[1155/678] w-[36.125rem] -translate-x-1/2 bg-gradient-to-tr from-[#ff80b5] to-[#9089fc] opacity-30 sm:left-[calc(50%+36rem)] sm:w-[72.1875rem]",
       :style
       "clip-path: polygon(74.1% 44.1%, 100% 61.6%, 97.5% 26.9%, 85.5% 0.1%, 80.7% 2%, 72.5% 32.5%, 60.2% 62.4%, 52.4% 68.1%, 47.5% 58.3%, 45.2% 34.5%, 27.5% 76.7%, 0.1% 64.9%, 17.9% 100%, 27.6% 76.8%, 76.1% 97.7%, 74.1% 44.1%)"}]]]])

(defn show-item [data]
  [:li
   {:class "flex justify-between gap-x-6 py-5"}
   [:div
    {:class "flex min-w-0 gap-x-4"}
    [:div
     {:class "min-w-0 flex-auto"}
     [:p
      {:class "text-sm font-semibold leading-6 text-gray-900"}
      (first data)]
     [:p {:class "mt-2 flex items-center text-sm text-gray-500"}
      (str "Gesamtkosten " (second data))]]]
   [:div
    {:class "hidden shrink-0 sm:flex sm:flex-col sm:items-end"}
    [:p {:class "text-sm leading-6 text-gray-900"} ]
    [:div
     {:class "mt-2 flex items-center text-sm text-gray-500"}
     [:svg
      {:class "mr-1.5 h-5 w-5 flex-shrink-0 text-gray-400",
       :viewBox "0 0 20 20",
       :fill "currentColor",
       :aria-hidden "true"}
      [:path
       {:d
        "M10.75 10.818v2.614A3.13 3.13 0 0011.888 13c.482-.315.612-.648.612-.875 0-.227-.13-.56-.612-.875a3.13 3.13 0 00-1.138-.432zM8.33 8.62c.053.055.115.11.184.164.208.16.46.284.736.363V6.603a2.45 2.45 0 00-.35.13c-.14.065-.27.143-.386.233-.377.292-.514.627-.514.909 0 .184.058.39.202.592.037.051.08.102.128.152z"}]
      [:path
       {:fill-rule "evenodd",
        :d
        "M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-6a.75.75 0 01.75.75v.316a3.78 3.78 0 011.653.713c.426.33.744.74.925 1.2a.75.75 0 01-1.395.55 1.35 1.35 0 00-.447-.563 2.187 2.187 0 00-.736-.363V9.3c.698.093 1.383.32 1.959.696.787.514 1.29 1.27 1.29 2.13 0 .86-.504 1.616-1.29 2.13-.576.377-1.261.603-1.96.696v.299a.75.75 0 11-1.5 0v-.3c-.697-.092-1.382-.318-1.958-.695-.482-.315-.857-.717-1.078-1.188a.75.75 0 111.359-.636c.08.173.245.376.54.569.313.205.706.353 1.138.432v-2.748a3.782 3.782 0 01-1.653-.713C6.9 9.433 6.5 8.681 6.5 7.875c0-.805.4-1.558 1.097-2.096a3.78 3.78 0 011.653-.713V4.75A.75.75 0 0110 4z",
        :clip-rule "evenodd"}]]
     (str "Insgesamt " (nth data 6))]]])

(defn show-details [data]
  [:div
   [:div {:id "upload-file" :class "mt-10 flex items-center justify-center gap-x-6"}
    [:form
     {:hx-encoding "multipart/form-data"
      :hx-post "/upload-details"
      :hx-target "this"
      :hx-swap "#upload-file"}
     [:input {:type "file", :name "file"}]
     [:button.inline-block.shrink-0.rounded-md.border.border-blue-600.bg-blue-600.px-12.py-3.text-sm.font-medium.text-white.transition.hover:bg-transparent.hover:text-blue-600.focus:outline-none.focus:ring.active:text-blue-500.dark:hover:bg-blue-700.dark:hover:text-white "Hochladen"]]]
   [:div {:class "mt-10 flex items-center justify-center gap-x-6"}
    [:ul {:role "list", :class "divide-y divide-gray-100"}
     (map show-item (:content data))]]])