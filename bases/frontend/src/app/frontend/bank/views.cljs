(ns app.frontend.bank.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.bank.subs :as subs]
            [app.frontend.bank.events :as events]
            ["/components/dashboard/BankDataUpload$default" :as upload-js]))

(def bank-data-upload (r/adapt-react-class upload-js))

(defn bank-data-upload-component []
  [bank-data-upload 
   {:id "bank-data-upload"
    :properties @(re-frame/subscribe [::subs/properties])
    :isAddPropertyDialogOpen @(re-frame/subscribe [::subs/add-propery-dialog-open])
    :propertyName @(re-frame/subscribe [::subs/form :name])
    :propertyAddress @(re-frame/subscribe [::subs/form :address])
    :propertyCity @(re-frame/subscribe [::subs/form :city])
    :propertyPostalCode @(re-frame/subscribe [::subs/form :postalcode])
    :propertyUnits @(re-frame/subscribe [::subs/form :units])
    :propertyPurchasePrice @(re-frame/subscribe [::subs/form :purchaseprice])
    :propertyCurrentValue @(re-frame/subscribe [::subs/form :currentvalue])
    :onChangeAddPropertyDialogOpen #(re-frame/dispatch [::events/show-add-property-dialog])
    :onChangeAddPropertyDialogClose #(re-frame/dispatch [::events/close-add-property-dialog])
    :onChangePropertyName #(re-frame/dispatch [::events/update-field :name (-> % .-target .-value)])
    :onChangePropertyAddress #(re-frame/dispatch [::events/update-field :address (-> % .-target .-value)])
    :onChangePropertyCity #(re-frame/dispatch [::events/update-field :city (-> % .-target .-value)])
    :onChangePropertyPostalCode #(re-frame/dispatch [::events/update-field :postalcode (-> % .-target .-value)])
    :onChangePropertyUnits #(re-frame/dispatch [::events/update-field :units (-> % .-target .-value)])
    :onChangePropertyPurchasePrice #(re-frame/dispatch [::events/update-field :purchaseprice (-> % .-target .-value)])
    :onChangePropertyCurrentValue #(re-frame/dispatch [::events/update-field :currentvalue (-> % .-target .-value)])
    :submitProperty #(re-frame/dispatch [::events/save-property])}])