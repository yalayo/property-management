(ns app.frontend.property.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.property.subs :as subs]
            [app.frontend.property.events :as events]
            ["/components/dashboard/PropertyList$default" :as property-list-js]
            ["/components/dashboard/AddProperty$default" :as add-property-js]))

(def property-list (r/adapt-react-class property-list-js))
(def add-property (r/adapt-react-class add-property-js))

(defn property-list-component []
  (re-frame/dispatch [::events/get-properties])
  
  [property-list 
   {:id "properties"
    :properties @(re-frame/subscribe [::subs/properties])
    :isAddPropertyDialogOpen @(re-frame/subscribe [::subs/add-propery-dialog-open])
    :onChangeAddPropertyDialogOpen #(re-frame/dispatch [::events/show-add-property-dialog])}
   [add-property
    {:id "add-property"
     :isAddPropertyDialogOpen @(re-frame/subscribe [::subs/add-propery-dialog-open])
     :propertyName @(re-frame/subscribe [::subs/form :name])
     :propertyAddress @(re-frame/subscribe [::subs/form :address])
     :propertyCity @(re-frame/subscribe [::subs/form :city])
     :propertyPostalCode @(re-frame/subscribe [::subs/form :postalcode])
     :propertyUnits @(re-frame/subscribe [::subs/form :units])
     :propertyPurchasePrice @(re-frame/subscribe [::subs/form :purchaseprice])
     :propertyCurrentValue @(re-frame/subscribe [::subs/form :currentvalue])
     :onChangeAddPropertyDialogClose #(re-frame/dispatch [::events/close-add-property-dialog])
     :onChangePropertyName #(re-frame/dispatch [::events/update-field :name (-> % .-target .-value)])
     :onChangePropertyAddress #(re-frame/dispatch [::events/update-field :address (-> % .-target .-value)])
     :onChangePropertyCity #(re-frame/dispatch [::events/update-field :city (-> % .-target .-value)])
     :onChangePropertyPostalCode #(re-frame/dispatch [::events/update-field :postalcode (-> % .-target .-value)])
     :onChangePropertyUnits #(re-frame/dispatch [::events/update-field :units (-> % .-target .-value)])
     :onChangePropertyPurchasePrice #(re-frame/dispatch [::events/update-field :purchaseprice (-> % .-target .-value)])
     :onChangePropertyCurrentValue #(re-frame/dispatch [::events/update-field :currentvalue (-> % .-target .-value)])
     :submitProperty #(re-frame/dispatch [::events/save-property])
     :addNewTenant #(re-frame/dispatch [::events/add-tenant])}]])