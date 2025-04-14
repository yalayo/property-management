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
    :onChangePropertyName #(re-frame/dispatch [::events/update-field :property-name (-> % .-target .-value)])}])