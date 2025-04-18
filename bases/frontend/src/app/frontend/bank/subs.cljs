(ns app.frontend.bank.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::is-loading
 (fn [db]
   (get-in db [:bank :data :is-loading])))

(re-frame/reg-sub
 ::transactions
 (fn [db]
   (get-in db [:bank :transactions])))