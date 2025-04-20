(ns app.frontend.tenant.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::tenants
 (fn [db]
   (get-in db [:tenant :tenants])))

(re-frame/reg-sub
 ::form
 (fn [db [_ id]]
   (get-in db [:tenant :form id] "")))

(re-frame/reg-sub
 ::add-tenant-dialog-open
 (fn [db]
   (get-in db [:tenant :add-tenant-dialog-open] false)))