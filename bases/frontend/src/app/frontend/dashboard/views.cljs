(ns app.frontend.dashboard.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.dashboard.events :as events]
            [app.frontend.dashboard.subs :as subs]
            [app.frontend.property.subs :as property-subs]
            [app.frontend.property.views :as property]
            [app.frontend.bank.views :as bank]
            [app.frontend.account.subs :as account-subs]
            [app.frontend.account.views :as account]
            [app.frontend.tenant.subs :as tenant-subs]
            [app.frontend.tenant.views :as tenant]
            [app.frontend.apartment.views :as apartment]
            ["/pages/dashboard$default" :as dashboard-js]
            ["/components/dashboard/DashboardSummary$default" :as dashboard-summary-js]))

(def dashboard (r/adapt-react-class dashboard-js))
(def dashboard-summary (r/adapt-react-class dashboard-summary-js))

(defn dashboard-component []
  (let [active-tab @(re-frame/subscribe [::subs/active-tab])
        properties @(re-frame/subscribe [::property-subs/properties])
        tenants @(re-frame/subscribe [::tenant-subs/tenants])
        accounts @(re-frame/subscribe [::account-subs/accounts])]
    [dashboard
     {:activeTab active-tab
      :onChangeActiveTab #(re-frame/dispatch [::events/change-active-tab %])
      :submitLogout #(re-frame/dispatch [::events/log-out %])}
     [dashboard-summary 
      {:id "dashboard-summary"
       :properties properties
       :tenants tenants
       :accounts accounts}]
     (property/property-list-component)
     (bank/bank-data-upload-component)
     (tenant/tenants-list-component)
     (account/accounts-list-component)
     (apartment/apartments-component properties)]))