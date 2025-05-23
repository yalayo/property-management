(ns app.frontend.property.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.property.subs :as subs]
            [app.frontend.property.events :as events]
            ["/components/dashboard/PropertyList$default" :as property-list-js]
            ["/components/dashboard/AddProperty$default" :as add-property-js]
            ["/pages/property/ManageProperty$default" :as manage-property-js]))

(def property-list (r/adapt-react-class property-list-js))
(def add-property (r/adapt-react-class add-property-js))
(def manage-property (r/adapt-react-class manage-property-js))

(defn property-list-component []
  (let [select-property? @(re-frame/subscribe [::subs/selected-property])]
    (if select-property?
      [manage-property
       {:id "properties"
        :selectedProperty @(re-frame/subscribe [::subs/selected-property])
        :electricity @(re-frame/subscribe [::subs/property-electricity])
        :editElectricity @(re-frame/subscribe [::subs/edit-field :electricity])
        :onEditElectricity #(re-frame/dispatch [::events/edit-field :electricity true])
        :cancelEditElectricity #(re-frame/dispatch [::events/edit-field :electricity %])
        :onChangePropertyElectricity #(re-frame/dispatch [::events/update-data :electricity %])
        :accountability @(re-frame/subscribe [::subs/property-accountability])
        :editAccountability @(re-frame/subscribe [::subs/edit-field :accountability])
        :onEditAccountability #(re-frame/dispatch [::events/edit-field :accountability true])
        :cancelEditAccountability #(re-frame/dispatch [::events/edit-field :accountability %])
        :onChangePropertyAccountability #(re-frame/dispatch [::events/update-data :accountability %])
        :tax @(re-frame/subscribe [::subs/property-tax])
        :editTax @(re-frame/subscribe [::subs/edit-field :property-tax])
        :onEditTax #(re-frame/dispatch [::events/edit-field :property-tax true])
        :cancelEditTax #(re-frame/dispatch [::events/edit-field :property-tax %])
        :onChangePropertyTax #(re-frame/dispatch [::events/update-data :property-tax %])
        :garbage @(re-frame/subscribe [::subs/property-garbage])
        :editGarbage @(re-frame/subscribe [::subs/edit-field :garbage])
        :onEditGarbage #(re-frame/dispatch [::events/edit-field :garbage true])
        :cancelEditGarbage #(re-frame/dispatch [::events/edit-field :garbage %])
        :onChangePropertyGarbage #(re-frame/dispatch [::events/update-data :garbage %])
        :rainwater @(re-frame/subscribe [::subs/property-rainwater])
        :editRainwater @(re-frame/subscribe [::subs/edit-field :rain-water])
        :onEditRainwater #(re-frame/dispatch [::events/edit-field :rain-water true])
        :cancelEditRainwater #(re-frame/dispatch [::events/edit-field :rain-water %])
        :onChangePropertyRainwater #(re-frame/dispatch [::events/update-data :rain-water %])
        :wastewater @(re-frame/subscribe [::subs/property-wastewater])
        :editWastewater @(re-frame/subscribe [::subs/edit-field :waste-water])
        :onEditWastewater #(re-frame/dispatch [::events/edit-field :waste-water true])
        :cancelEditWastewater #(re-frame/dispatch [::events/edit-field :waste-water %])
        :onChangePropertyWastewater #(re-frame/dispatch [::events/update-data :waste-water %])
        :drinkingwater @(re-frame/subscribe [::subs/property-drinkingwater])
        :editDrinkingwater @(re-frame/subscribe [::subs/edit-field :drinking-water])
        :onEditDrinkingwater #(re-frame/dispatch [::events/edit-field :drinking-water true])
        :cancelEditDrinkingwater #(re-frame/dispatch [::events/edit-field :drinking-water %])
        :onChangePropertyDrinkingwater #(re-frame/dispatch [::events/update-data :drinking-water %])
        :onCancel #(re-frame/dispatch [::events/cancel])}]
      [property-list
       {:id "properties"
        :properties @(re-frame/subscribe [::subs/properties])
        :isAddPropertyDialogOpen @(re-frame/subscribe [::subs/add-property-dialog-open])
        :onChangeAddPropertyDialogOpen #(re-frame/dispatch [::events/show-add-property-dialog])
        :addNewTenant #(re-frame/dispatch [::events/add-tenant])
        :onManageProperty #(re-frame/dispatch [::events/manage-property %])}
       [add-property
        {:id "add-property"
         :isAddPropertyDialogOpen @(re-frame/subscribe [::subs/add-property-dialog-open])
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
         :submitProperty #(re-frame/dispatch [::events/save-property])}]])))