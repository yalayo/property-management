(ns app.frontend.property.events
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
   (assoc-in db [:property :form id] val)))

(re-frame/reg-event-fx
 ::save-property
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/new-property")
                 :params          (get-in db [:property :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::property-submitted]
                 :on-failure      [::property-creation-error]}}))

(re-frame/reg-event-db
 ::property-submitted
 (fn [db [_ response]]
   (js/console.log "Property saved:" response)
   (-> db
       (assoc-in [:property :form] nil)
       (assoc-in [:property ::add-propery-dialog-open] false))))

(re-frame/reg-event-fx
 ::property-creation-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt property:" error)
   {}))

(re-frame/reg-event-fx
 ::get-properties
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (str config/api-url "/api/properties")
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::update-db]
                 :on-failure      [::get-properties-error]}}))

(re-frame/reg-event-db
 ::update-db
 [local-storage-interceptor]
 (fn [db [_ response]]
   (assoc-in db [:property :properties] response)))

(re-frame/reg-event-fx
 ::get-properties-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to get the list of properties:" error)
   {}))

(re-frame/reg-event-db
 ::show-add-property-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:property :add-propery-dialog-open] true)))

(re-frame/reg-event-db
 ::close-add-property-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:property :add-propery-dialog-open] false)))

(re-frame/reg-event-db
 ::add-tenant
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:dashboard :active-tab] "new-tenant")))