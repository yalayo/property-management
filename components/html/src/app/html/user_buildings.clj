(ns app.html.user-buildings)

(def data  {:cur-user "Current user"
            :buildings [{:id 1 :name "Building 1" :address "Address 1" :pic "https://unsplash.com/es/fotos/una-gran-casa-blanca-sentada-al-costado-de-una-carretera-27dyESJ0RIk"}
                        {:id 2 :name "Building 2" :address "Address 2" :pic "https://unsplash.com/es/fotos/un-patio-con-fogata-y-zona-de-estar-E_6gn_IdJMs"}
                        {:id 3 :name "Building 3" :address "Address 3" :pic "https://unsplash.com/es/fotos/una-vista-aerea-de-una-casa-en-el-otono-ljnOUFtwzFA"}
                        {:id 4 :name "Building 4" :address "Address 4" :pic "https://unsplash.com/es/fotos/una-piscina-con-tumbonas-a-su-alrededor-rCHO0tFxLlk"}
                        {:id 5 :name "Building 5" :address "Address 5" :pic "https://unsplash.com/es/fotos/una-vista-aerea-de-una-casa-en-el-otono-pWgdeJMKDEM"}
                        {:id 6 :name "Building 6" :address "Address 6" :pic "https://unsplash.com/es/fotos/vista-aerea-de-una-casa-con-piscina-USpH76SXqS8"}
                        {:id 7 :name "Building 7" :address "Address 7" :pic "https://unsplash.com/es/fotos/una-casa-en-el-bosque-con-un-camino-que-conduce-a-ella-ysM9uXVGpyw"}
                        {:id 8 :name "Building 8" :address "Address 8" :pic "https://unsplash.com/es/fotos/una-sauna-de-madera-en-medio-de-una-zona-boscosa-30Rta33lHrk"}
                        {:id 9 :name "Building 9" :address "Address 9" :pic "https://unsplash.com/es/fotos/una-pequena-cabana-en-el-bosque-con-una-cubierta-5Bnb9Ec07HI"}
                        {:id 10 :name "Building 10" :address "Address 10" :pic "https://unsplash.com/es/fotos/una-gran-casa-blanca-con-una-puerta-roja-yIZZucnwJWA"}]})

(defn get-buildings []
  [:div.container.mx-auto
   [:h1 {:class "text-2xl font-semibold text-gray-900"} (str (:cur-user data) " s'Buildings:")]
   [:ul
    {:role "list", :class "divide-y divide-gray-100"}
    (for [building (:buildings data)]
      [:li
       {:class "flex justify-between gap-x-6 py-5"}
       [:div
        {:class "flex min-w-0 gap-x-4"}
        [:img
         {:class "w-48 h-48 flex-none rounded-3xl bg-gray-50",
          :src (:pic building),
          :alt "Picture of building"}]
        [:div
         {:class "min-w-0 flex-auto"}
         [:p
          {:class "text-sm/6 font-semibold text-gray-900"}
          (:name building)]
         [:p
          {:class "mt-1 truncate text-xs/5 text-gray-500"}
          (:address building)]]]
       [:div
        {:class "hidden shrink-0 sm:flex sm:flex-col sm:items-end"}
        [:p {:class "text-sm/6 text-gray-900"}
         "Other data here!"]
        [:p
         {:class "mt-1 text-xs/5 text-gray-500"}
         "Other data here!"]]])]])