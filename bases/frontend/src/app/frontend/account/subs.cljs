(ns app.frontend.account.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::accounts
 (fn [db]
   (get-in db [:account :accounts])))

(re-frame/reg-sub
 ::form
 (fn [db [_ id]]
   (get-in db [:account :form id] "")))

(re-frame/reg-sub
 ::add-account-dialog-open
 (fn [db]
   (get-in db [:account :add-account-dialog-open] false)))

(re-frame/reg-sub
 ::selected-account
 (fn [db]
   (some? (get-in db [:account :selected-account]))))

(re-frame/reg-sub
 ::is-loading
 (fn [db]
   (get-in db [:account :data :is-loading] false)))

(re-frame/reg-sub
 ::transactions
 (fn [db]
   (get-in db [:account :transactions])))