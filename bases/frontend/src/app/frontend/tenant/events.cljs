(ns app.frontend.tenant.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [app.frontend.events :as main-events]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(def local-storage-interceptor main-events/->local-store)

(re-frame/reg-event-db
 ::update-field
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:tenant :form id] val)))

(re-frame/reg-event-fx
 ::save-tenant
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/new-tenant")
                 :params          (get-in db [:tenant :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::tenant-submitted]
                 :on-failure      [::tenant-creation-error]}}))

(re-frame/reg-event-db
 ::tenant-submitted
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [tenants (get-in db [:tenant :tenants])
         tenant (get-in db [:tenant :form])
         updated (conj tenants tenant)]
     (js/console.log "Tenant saved:" response)
     (-> db
         (assoc-in [:tenant :tenants] updated)
         (assoc-in [:tenant :form] nil)
         (assoc-in [:tenant :add-tenant-dialog-open] false)))))

(re-frame/reg-event-fx
 ::tenant-creation-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt tenant:" error)
   {}))

(re-frame/reg-event-fx
 ::get-tenants
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (str config/api-url "/api/tenants")
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::update-db]
                 :on-failure      [::get-tenants-error]}}))

(re-frame/reg-event-db
 ::update-db
 [local-storage-interceptor]
 (fn [db [_ response]]
   (assoc-in db [:tenant :tenants] response)))

(re-frame/reg-event-fx
 ::get-tenants-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to get the list of tenants:" error)
   {}))

(re-frame/reg-event-db
 ::show-add-tenant-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:tenant :add-tenant-dialog-open] true)))

(re-frame/reg-event-db
 ::close-add-tenant-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:tenant :add-tenant-dialog-open] false)))