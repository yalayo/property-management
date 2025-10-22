(ns app.frontend.letter.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.letter.subs :as subs]
            [app.frontend.letter.events :as events]
            ["/pages/letter/LetterDataUpload$default" :as upload-js]))

(def letter-data-upload (r/adapt-react-class upload-js))

(defn letter-data-upload-component []
  [letter-data-upload 
   {:id "letters"
    :isLoading @(re-frame/subscribe [::subs/is-loading])
    :tenants @(re-frame/subscribe [::subs/tenants])
    :errors @(re-frame/subscribe [::subs/errors])
    :onUploadData #(re-frame/dispatch [::events/upload-data (-> % .-target .-files (aget 0))])
    :onCreateLetter #(re-frame/dispatch [::events/create-letter %])
    :onSendAllLetters #(re-frame/dispatch [::events/send-letters %])}])