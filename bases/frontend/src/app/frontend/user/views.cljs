(ns app.frontend.user.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.user.subs :as subs]
            [app.frontend.user.events :as events]
            ["/pages/login$default" :as login-js]
            ["/pages/register$default" :as register-js]))

(def login (r/adapt-react-class login-js))
(def register (r/adapt-react-class register-js))

(defn login-component []
  [login 
   {:user @(re-frame/subscribe [::subs/sign-in-form :user])
    :onChangeUser #(re-frame/dispatch [::events/update-sign-in :user (-> % .-target .-value)])
    :password @(re-frame/subscribe [::subs/sign-in-form :password])
    :onChangePassword #(re-frame/dispatch [::events/update-sign-in :password (-> % .-target .-value)])
    :submitLogin #(re-frame/dispatch [::events/sign-in])}])