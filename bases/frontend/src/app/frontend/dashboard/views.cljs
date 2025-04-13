(ns app.frontend.dashboard.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            ["/pages/dashboard$default" :as dashboard-js]
            ["/components/dashboard/PropertyList$default" :as property-list-js]))

(def dashboard (r/adapt-react-class dashboard-js))
(def property-list (r/adapt-react-class property-list-js))

(defn dashboard-component []
  [dashboard {:activeTab "properties"}
   [property-list {:id "properties"}]])