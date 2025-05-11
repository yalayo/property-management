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

(re-frame/reg-sub
 ::selected-property
 (fn [db]
   (let [selected-id (get-in db [:property :selected-property])
         properties     (get-in db [:property :properties])]
     (some #(when (= (:id %) selected-id) (:name %)) properties))))

(re-frame/reg-sub
 ::edit-field
 (fn [db [_ id]]
   (get-in db [:property id :edit] false)))

(re-frame/reg-sub
 ::property-electricity
 (fn [db]
   (get-in db [:property :electricity :value])))

(re-frame/reg-sub
 ::property-accountability
 (fn [db]
   (get-in db [:property :accountability :value])))

(re-frame/reg-sub
 ::property-tax
 (fn [db]
   (get-in db [:property :tax :value])))

(re-frame/reg-sub
 ::property-garbage
 (fn [db]
   (get-in db [:property :garbage :value])))