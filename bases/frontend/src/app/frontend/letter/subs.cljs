(ns app.frontend.letter.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::is-loading
 (fn [db]
   (get-in db [:letter :data :is-loading])))

(re-frame/reg-sub
 ::tenants
 (fn [db]
   (get-in db [:letter :data :tenants])))

(re-frame/reg-sub
 ::errors
 (fn [db]
   (get-in db [:letter :data :errors])))