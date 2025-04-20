(ns app.frontend.tenant.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.tenant.subs :as subs]
            [app.frontend.tenant.events :as events]
            ["/pages/tenant-onboarding$default" :as tenant-onboarding-js]
            ["/pages/tenant/TenantsList$default" :as tenants-list-js]
            ["/pages/tenant/AddTenant$default" :as new-tenant-js]))

(def tenant-onboarding (r/adapt-react-class tenant-onboarding-js))
(def tenants-list (r/adapt-react-class tenants-list-js))
(def new-tenant (r/adapt-react-class new-tenant-js))

(defn tenants-list-component []
  #_(re-frame/dispatch [::events/get-tenants])

  [tenants-list
   {:id "tenants"
    :tenants @(re-frame/subscribe [::subs/tenants])
    :isAddTenantDialogOpen @(re-frame/subscribe [::subs/add-tenant-dialog-open])
    :onChangeAddTenantDialogOpen #(re-frame/dispatch [::events/show-add-tenant-dialog])}
   [new-tenant
    {:id "add-tenant"
     :isAddTenantDialogOpen @(re-frame/subscribe [::subs/add-tenant-dialog-open])
     :name @(re-frame/subscribe [::subs/form :name])
     :lastName @(re-frame/subscribe [::subs/form :lastname])
     :email @(re-frame/subscribe [::subs/form :email])
     :phone @(re-frame/subscribe [::subs/form :phone])
     :onChangeAddTenantDialogClose #(re-frame/dispatch [::events/close-add-tenant-dialog])
     :onChangeName #(re-frame/dispatch [::events/update-field :name (-> % .-target .-value)])
     :onChangeLastName #(re-frame/dispatch [::events/update-field :lastname (-> % .-target .-value)])
     :onChangeEmail #(re-frame/dispatch [::events/update-field :email (-> % .-target .-value)])
     :onChangePhone #(re-frame/dispatch [::events/update-field :phone (-> % .-target .-value)])
     :submitTenant #(re-frame/dispatch [::events/save-tenant])}]])