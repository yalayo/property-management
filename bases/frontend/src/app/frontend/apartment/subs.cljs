(ns app.frontend.apartment.subs
  (:require [re-frame.core :as re-frame]))

(re-frame.core/reg-sub
 ::apartments
 (fn [db]
   (let [apartments (get-in db [:apartment :apartments])
         tenants    (get-in db [:tenant :tenants])]
     (map (fn [apt]
            (if-let [tenant-id (:tenant apt)]
              (if-let [tenant (some #(when (= (:id %) tenant-id) %) tenants)]
                (assoc apt :tenant-name (str (:name tenant) " " (:lastname tenant)))
                apt)
              apt))
          apartments))))

(re-frame/reg-sub
 ::form
 (fn [db [_ id]]
   (get-in db [:apartment :form id] "")))

(re-frame/reg-sub
 ::add-apartment-dialog-open
 (fn [db]
   (get-in db [:apartment :add-apartment-dialog-open] false)))

(re-frame/reg-sub
 ::selected-apartment
 (fn [db]
   (some? (get-in db [:apartment :selected-apartment]))))

(re-frame/reg-sub
 ::selected-tenant
 (fn [db]
   (get-in db [:apartment :selected-tenant] "")))