(ns app.frontend.dashboard.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.dashboard.events :as events]
            [app.frontend.dashboard.subs :as subs]
            [app.frontend.property.views :as property]
            [app.frontend.bank.views :as bank]
            [app.frontend.tenant.views :as tenant]
            ["/pages/dashboard$default" :as dashboard-js]))

(def dashboard (r/adapt-react-class dashboard-js))

(defn dashboard-component []
  [dashboard 
   {:activeTab @(re-frame/subscribe [::subs/active-tab])
    :onChangeActiveTab #(re-frame/dispatch [::events/change-active-tab %])
    :submitLogout #(re-frame/dispatch [::events/log-out %])}
   (property/property-list-component)
   (bank/bank-data-upload-component)
   (tenant/tenants-list-component)])