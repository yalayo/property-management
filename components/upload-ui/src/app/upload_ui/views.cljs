(ns app.upload-ui.views
  (:require [reagent.core  :as r]
            ["/pages/letter/LetterDataUpload$default" :as upload-js]))

(def file-upload (r/adapt-react-class upload-js))

(defn file-upload-component []
  [:<>
   [:div.max-w-7xl.mx-auto.px-4.sm:px-6.lg:px-8.py-12
    [file-upload
     {:id "file-upload"
      :isLoading true}]]])


