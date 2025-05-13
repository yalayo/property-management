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

(defn last-year []
  (let [today (js/Date.)
        current-year (.getFullYear today)]
    (- current-year 1)))

(re-frame/reg-event-fx
 ::update-data
 [local-storage-interceptor]
 (fn [{:keys [db]} [_ id val]]
   (let [new-state (-> db
                       (assoc-in [:property id :value] val)
                       (assoc-in [:property id :edit] false))
         property-id (get-in db [:property :selected-property])
         amount (js/parseFloat val)
         data {:kind :expense :category id :year (last-year) :property property-id :amount amount}]
     {:db new-state
      :http-xhrio {:method          :post
                   :uri             (str config/api-url "/api/new-operation")
                   :params          data
                   :format          (ajax-edn/edn-request-format)
                   :response-format (ajax-edn/edn-response-format)
                   :timeout         8000
                   :on-success      [::property-data-updated]
                   :on-failure      [::property-data-update-error]}})))

(re-frame/reg-event-db
 ::property-data-updated
 [local-storage-interceptor]
 (fn [db [_ response]]
   (js/console.log "Property attribute updated:" response)))

(re-frame/reg-event-fx
 ::property-data-update-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to update property data:" error)
   {}))

(re-frame/reg-event-db
 ::edit-field
 [local-storage-interceptor]
 (fn [db [_ id]]
   (assoc-in db [:property id :edit] true)))

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
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [properties (get-in db [:property :properties])
         property (get-in db [:property :form])
         updated (conj properties property)]
     (js/console.log "Property saved:" response)
     (-> db
         (assoc-in [:property :properties] updated)
         (assoc-in [:property :form] nil)
         (assoc-in [:property :add-property-dialog-open] false)))))

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
   (assoc-in db [:property :add-property-dialog-open] true)))

(re-frame/reg-event-db
 ::close-add-property-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:property :add-property-dialog-open] false)))

(re-frame/reg-event-db
 ::add-tenant
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:dashboard :active-tab] "new-tenant")))

(re-frame/reg-event-db
 ::manage-property
 [local-storage-interceptor]
 (fn [db [_ val]]
   (assoc-in db [:property :selected-property] val)))

(re-frame/reg-event-db
 ::cancel
 [local-storage-interceptor]
 (fn [db [_ val]]
   (assoc-in db [:property :selected-property] nil)))