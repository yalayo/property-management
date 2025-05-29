(ns app.frontend.apartment.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.apartment.subs :as subs]
            [app.frontend.apartment.events :as events]
            [app.frontend.tenant.subs :as tenant-subs]
            ["/pages/apartment/ApartmentsList$default" :as apartments-list-js]
            ["/pages/apartment/AddApartment$default" :as new-apartment-js]
            ["/pages/apartment/ManageApartment$default" :as manage-apartment-js]
            ["/pages/tenant/SelectTenant$default" :as select-tenant-js]))

(def apartments-list (r/adapt-react-class apartments-list-js))
(def new-apartment (r/adapt-react-class new-apartment-js))
(def manage-apartment (r/adapt-react-class manage-apartment-js))
(def select-tenant (r/adapt-react-class select-tenant-js))

(defn apartments-component [properties]
  (let [data @(re-frame/subscribe [::subs/selected-apartment])
        select-apartment (:value data)
        action (:action data)]
    (if select-apartment
      (case action
        :assign-tenant [select-tenant
                        {:id "apartments"
                         :tenants @(re-frame/subscribe [::tenant-subs/tenants])
                         :selectedTenant @(re-frame/subscribe [::subs/selected-tenant])
                         :onSelectTenant #(re-frame/dispatch [::events/select-tenant %])
                         :onCancel #(re-frame/dispatch [::events/cancel])
                         :onSaveSelection #(re-frame/dispatch [::events/save-selection])}]
        :manage-apartment [manage-apartment
                           {:id "apartments"
                            :selectedApartment select-apartment
                            :onCancel #(re-frame/dispatch [::events/cancel])
                            :onSaveSelection #(re-frame/dispatch [::events/save-selection])}])
     
      [apartments-list
       {:id "apartments"
        :apartments @(re-frame/subscribe [::subs/apartments])
        :isAddApartmentDialogOpen @(re-frame/subscribe [::subs/add-apartment-dialog-open])
        :onChangeAddApartmentDialogOpen #(re-frame/dispatch [::events/show-add-apartment-dialog])
        :onAssignTenant #(re-frame/dispatch [::events/assign-tenant %])
        :onManageApartment #(re-frame/dispatch [::events/manage-apartment %])}
       [new-apartment
        {:id "add-apartment"
         :properties properties
         :isAddApartmentDialogOpen @(re-frame/subscribe [::subs/add-apartment-dialog-open])
         :code @(re-frame/subscribe [::subs/form :code])
         :property @(re-frame/subscribe [::subs/form :property])
         :email @(re-frame/subscribe [::subs/form :email])
         :phone @(re-frame/subscribe [::subs/form :phone])
         :onChangeAddApartmentDialogClose #(re-frame/dispatch [::events/close-add-apartment-dialog])
         :onChangeCode #(re-frame/dispatch [::events/update-field :code (-> % .-target .-value)])
         :onChangeProperty #(re-frame/dispatch [::events/update-field :property %])
         :onChangeEmail #(re-frame/dispatch [::events/update-field :email (-> % .-target .-value)])
         :onChangePhone #(re-frame/dispatch [::events/update-field :phone (-> % .-target .-value)])
         :submitApartment #(re-frame/dispatch [::events/save-apartment])}]])))