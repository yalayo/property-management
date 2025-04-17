(ns app.frontend.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.events :as events]
            [app.frontend.subs :as subs]
            [app.frontend.user.views :as user-view]
            ["/pages/home$default" :as home]
            [app.frontend.landing.views :as landing]
            [app.frontend.survey.views :as survey]
            [app.frontend.dashboard.views :as dashboard]))

;; Testing the login component
#_(defn app []
  [:<>
   [user-view/login-component]])

(def home-component (r/adapt-react-class home))

#_(defn app []
  [:<>
   [home-component {:isLoggedIn false :user {}}
    (survey/survey-component)]])

;; Testing the dashboard component
#_(defn app []
  [:<>
   (dashboard/dashboard-component)])

;; Testing the landing page
(defn app []
  [:<>
   (landing/landing-component)])