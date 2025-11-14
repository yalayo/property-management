(ns app.upload-ui.views
  (:require [reagent.core  :as r]
            ["/pages/letter/LetterDataUpload$default" :as upload-js]))

(def file-upload (r/adapt-react-class upload-js))

(defn file-upload-component []
  [file-upload
   {:id "file-upload"
    :isLoading true}])



