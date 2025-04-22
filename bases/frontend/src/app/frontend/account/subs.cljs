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