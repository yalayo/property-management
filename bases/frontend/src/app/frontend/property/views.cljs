(ns app.frontend.property.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.property.subs :as subs]
            [app.frontend.property.events :as events]
            ["/components/dashboard/PropertyList$default" :as property-list-js]))

(def property-list (r/adapt-react-class property-list-js))

(defn property-list-component []
  [property-list 
   {:id "properties"
    :propertyName @(re-frame/subscribe [::subs/form :name])
    :propertyAddress @(re-frame/subscribe [::subs/form :address])
    :propertyCity @(re-frame/subscribe [::subs/form :city])
    :propertyPostalCode @(re-frame/subscribe [::subs/form :postal-code])
    :propertyUnits @(re-frame/subscribe [::subs/form :units])
    :propertyPurchasePrice @(re-frame/subscribe [::subs/form :purchase-price])
    :propertyCurrentValue @(re-frame/subscribe [::subs/form :current-value])
    :onChangePropertyName #(re-frame/dispatch [::events/update-field :name (-> % .-target .-value)])
    :onChangePropertyAddress #(re-frame/dispatch [::events/update-field :address (-> % .-target .-value)])
    :onChangePropertyCity #(re-frame/dispatch [::events/update-field :city (-> % .-target .-value)])
    :onChangePropertyPostalCode #(re-frame/dispatch [::events/update-field :postal-code (-> % .-target .-value)])
    :onChangePropertyUnits #(re-frame/dispatch [::events/update-field :units (-> % .-target .-value)])
    :onChangePropertyPurchasePrice #(re-frame/dispatch [::events/update-field :purchase-price (-> % .-target .-value)])
    :onChangePropertyCurrentValue #(re-frame/dispatch [::events/update-field :current-value (-> % .-target .-value)])
    :submitProperty #(re-frame/dispatch [::events/save-property])}])