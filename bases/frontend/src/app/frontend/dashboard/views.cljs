(ns app.frontend.dashboard.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.dashboard.events :as events]
            [app.frontend.dashboard.subs :as subs]
            [app.frontend.property.views :as property]
            ["/pages/dashboard$default" :as dashboard-js]))

(def dashboard (r/adapt-react-class dashboard-js))

(defn dashboard-component []
  [dashboard 
   {:activeTab @(re-frame/subscribe [::subs/active-tab])
    :onChangeActiveTab #(re-frame/dispatch [::events/change-active-tab %])}
   (property/property-list-component)])