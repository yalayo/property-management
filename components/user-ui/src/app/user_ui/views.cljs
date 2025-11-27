(ns app.user-ui.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.user-ui.subs :as subs]
            [app.user-ui.events :as events]
            ["/pages/login$default" :as login-js]
            ["/pages/register$default" :as register-js]))

(def login (r/adapt-react-class login-js))
(def register (r/adapt-react-class register-js))

(defn access-component []
  (let [active-form @(re-frame/subscribe [::subs/active-form])]
    (case active-form
      :sign-in [login {:user @(re-frame/subscribe [::subs/sign-in-form :user]) 
                       :onChangeUser #(re-frame/dispatch [::events/update-sign-in :user (-> % .-target .-value)]) 
                       :password @(re-frame/subscribe [::subs/sign-in-form :password]) 
                       :onChangePassword #(re-frame/dispatch [::events/update-sign-in :password (-> % .-target .-value)]) 
                       :submitLogin #(re-frame/dispatch [::events/sign-in]) 
                       :showSignUp #(re-frame/dispatch [::events/show-sign-up])}]
      :sign-up [register {:name @(re-frame/subscribe [::subs/sign-up-form :name]) 
                          :onChangeName #(re-frame/dispatch [::events/update-sign-up :name (-> % .-target .-value)]) 
                          :user @(re-frame/subscribe [::subs/sign-up-form :user]) 
                          :onChangeUser #(re-frame/dispatch [::events/update-sign-up :user (-> % .-target .-value)]) 
                          :password @(re-frame/subscribe [::subs/sign-up-form :password]) 
                          :onChangePassword #(re-frame/dispatch [::events/update-sign-up :password (-> % .-target .-value)]) 
                          :submitRegister #(re-frame/dispatch [::events/sign-up])
                          :showSignIn #(re-frame/dispatch [::events/show-sign-in])}])))