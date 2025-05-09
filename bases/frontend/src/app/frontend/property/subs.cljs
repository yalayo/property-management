(ns app.frontend.property.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::properties
 (fn [db]
   (get-in db [:property :properties])))

(re-frame/reg-sub
 ::form
 (fn [db [_ id]]
   (get-in db [:property :form id] "")))

(re-frame/reg-sub
 ::add-property-dialog-open
 (fn [db]
   (get-in db [:property :add-property-dialog-open] false)))

(re-frame/reg-sub
 ::selected-property
 (fn [db]
   (some? (get-in db [:property :selected-property]))))