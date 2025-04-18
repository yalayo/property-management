(ns app.frontend.views
  (:require [app.frontend.landing.views :as landing]))

(defn app []
  [:<>
   (landing/landing-component)])