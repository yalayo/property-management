(ns app.frontend.apartment.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.apartment.subs :as subs]
            [app.frontend.apartment.events :as events]
            ["/pages/apartment/ApartmentsList$default" :as apartments-list-js]
            ["/pages/apartment/AddApartment$default" :as new-apartment-js]))

(def apartments-list (r/adapt-react-class apartments-list-js))
(def new-apartment (r/adapt-react-class new-apartment-js))

(defn apartments-component [properties]
  #_(re-frame/dispatch [::events/get-apartments])

  [apartments-list
   {:id "apartments"
    :apartments @(re-frame/subscribe [::subs/apartments])
    :isAddApartmentDialogOpen @(re-frame/subscribe [::subs/add-apartment-dialog-open])
    :onChangeAddApartmentDialogOpen #(re-frame/dispatch [::events/show-add-apartment-dialog])}
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
     :submitApartment #(re-frame/dispatch [::events/save-apartment])}]])