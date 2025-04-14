(ns app.frontend.dashboard.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.property.views :as property]
            ["/pages/dashboard$default" :as dashboard-js]))

(def dashboard (r/adapt-react-class dashboard-js))

(defn dashboard-component []
  [dashboard {:activeTab "properties"}
   (property/property-list-component)])