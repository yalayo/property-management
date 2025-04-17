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

(def home-component (r/adapt-react-class home))

;; Testing the login component
(defn app []
  [:<>
   [user-view/login-component]
   [user-view/register-component]])

#_(defn app []
  [:<>
   [home-component {:isLoggedIn false :user {}}
    (survey/survey-component)]])

;; Testing the dashboard component
#_(defn app []
  [:<>
   (dashboard/dashboard-component)])

;; Testing the landing page
#_(defn app []
  [:<>
   (landing/landing-component)])