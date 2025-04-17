(ns app.frontend.landing.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            ["/components/landing/Header$default" :as header-js]
            ["/components/landing/Footer$default" :as footer-js]
            ["/components/landing/Hero$default" :as hero-js]
            ["/components/landing/Features$default" :as features-js]
            ["/components/landing/Pricing$default" :as pricing-js]))

(def header (r/adapt-react-class header-js))
(def footer (r/adapt-react-class footer-js))
(def hero (r/adapt-react-class hero-js))
(def features (r/adapt-react-class features-js))
(def pricing (r/adapt-react-class pricing-js))

(defn landing-component []
  [:<>
   [header]
   [hero]
   [features]
   [pricing]
   [footer]])