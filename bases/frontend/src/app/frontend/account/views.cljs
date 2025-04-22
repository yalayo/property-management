(ns app.frontend.account.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.account.subs :as subs]
            [app.frontend.account.events :as events]
            ["/pages/account/AccountsList$default" :as accounts-list-js]
            ["/pages/account/AddAccount$default" :as new-account-js]))

(def accounts-list (r/adapt-react-class accounts-list-js))
(def new-account (r/adapt-react-class new-account-js))

(defn accounts-list-component []
  [accounts-list
   {:id "accounts"
    :accounts @(re-frame/subscribe [::subs/accounts])
    :isAddAccountDialogOpen @(re-frame/subscribe [::subs/add-account-dialog-open])
    :onChangeAddAccountDialogOpen #(re-frame/dispatch [::events/show-add-account-dialog])}
   [new-account
    {:id "add-account"
     :isAddAccountDialogOpen @(re-frame/subscribe [::subs/add-account-dialog-open])
     :name @(re-frame/subscribe [::subs/form :name])
     :lastName @(re-frame/subscribe [::subs/form :lastname])
     :email @(re-frame/subscribe [::subs/form :email])
     :phone @(re-frame/subscribe [::subs/form :phone])
     :onChangeAddAccountDialogClose #(re-frame/dispatch [::events/close-add-account-dialog])
     :onChangeName #(re-frame/dispatch [::events/update-field :name (-> % .-target .-value)])
     :onChangeLastName #(re-frame/dispatch [::events/update-field :lastname (-> % .-target .-value)])
     :onChangeEmail #(re-frame/dispatch [::events/update-field :email (-> % .-target .-value)])
     :onChangePhone #(re-frame/dispatch [::events/update-field :phone (-> % .-target .-value)])
     :submitAccount #(re-frame/dispatch [::events/save-account])}]])