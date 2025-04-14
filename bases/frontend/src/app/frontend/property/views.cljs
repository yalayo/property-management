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
    :propertyName @(re-frame/subscribe [::subs/form :property-name])
    :propertyAddress @(re-frame/subscribe [::subs/form :property-address])
    :propertyCity @(re-frame/subscribe [::subs/form :property-city])
    :propertyPostalCode @(re-frame/subscribe [::subs/form :property-postal-code])
    :propertyUnits @(re-frame/subscribe [::subs/form :property-units])
    :propertyPurchasePrice @(re-frame/subscribe [::subs/form :property-purchase-price])
    :propertyCurrentValue @(re-frame/subscribe [::subs/form :property-current-value])
    :onChangePropertyName #(re-frame/dispatch [::events/update-field :property-name (-> % .-target .-value)])
    :onChangePropertyAddress #(re-frame/dispatch [::events/update-field :property-address (-> % .-target .-value)])
    :onChangePropertyCity #(re-frame/dispatch [::events/update-field :property-city (-> % .-target .-value)])
    :onChangePropertyPostalCode #(re-frame/dispatch [::events/update-field :property-postal-code (-> % .-target .-value)])
    :onChangePropertyUnits #(re-frame/dispatch [::events/update-field :property-units (-> % .-target .-value)])
    :onChangePropertyPurchasePrice #(re-frame/dispatch [::events/update-field :property-purchase-price (-> % .-target .-value)])
    :onChangePropertyCurrentValue #(re-frame/dispatch [::events/update-field :property-current-value (-> % .-target .-value)])
    :submitProperty #(re-frame/dispatch [::events/save-property])}])