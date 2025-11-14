(ns app.web.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            ["/pages/home-page$default" :as home-js]))

(def home (r/adapt-react-class home-js))

(defn landing-component []
  [:<>
   [home]])