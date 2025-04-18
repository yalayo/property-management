(ns app.frontend.bank.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.bank.subs :as subs]
            [app.frontend.bank.events :as events]
            ["/components/dashboard/BankDataUpload$default" :as upload-js]))

(def bank-data-upload (r/adapt-react-class upload-js))

(defn bank-data-upload-component []
  [bank-data-upload 
   {:id "documents"
    :isLoading @(re-frame/subscribe [::subs/is-loading])
    :transactions @(re-frame/subscribe [::subs/transactions])
    :onUploadData #(re-frame/dispatch [::events/upload-data (-> % .-target .-files (aget 0))])}])