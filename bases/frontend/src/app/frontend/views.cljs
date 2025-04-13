(ns app.frontend.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.events :as events]
            [app.frontend.subs :as subs]
            [app.frontend.user.views :as user-view]
            ["/pages/home$default" :as home]
            [app.frontend.survey.views :as survey]))

#_(defn app []
  [:<>
   [user-view/login-component]])

(def home-component (r/adapt-react-class home))

(defn app []
  [:<>
   [home-component {:isLoggedIn false :user {}}
    (survey/survey-component)]])