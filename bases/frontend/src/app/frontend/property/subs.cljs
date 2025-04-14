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