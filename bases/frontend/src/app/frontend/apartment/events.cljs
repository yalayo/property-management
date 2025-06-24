(ns app.frontend.apartment.events
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
   (assoc-in db [:apartment :form id] val)))

(re-frame/reg-event-fx
 ::save-apartment
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/new-apartment")
                 :params          (get-in db [:apartment :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::apartment-submitted]
                 :on-failure      [::apartment-creation-error]}}))

(re-frame/reg-event-db
 ::apartment-submitted
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [apartments (get-in db [:apartment :apartments])
         apartment (get-in db [:apartment :form])
         updated (conj apartments apartment)]
     (js/console.log "Apartment saved:" response)
     (-> db
         (assoc-in [:apartment :apartments] updated)
         (assoc-in [:apartment :form] nil)
         (assoc-in [:apartment :add-apartment-dialog-open] false)))))

(re-frame/reg-event-fx
 ::apartment-creation-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt apartment:" error)
   {}))

(re-frame/reg-event-fx
 ::get-apartments
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (str config/api-url "/api/apartments")
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::update-db]
                 :on-failure      [::get-apartments-error]}}))

(re-frame/reg-event-db
 ::update-db
 [local-storage-interceptor]
 (fn [db [_ response]]
   (assoc-in db [:apartment :apartments] response)))

(re-frame/reg-event-fx
 ::get-apartments-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to get the list of apartments:" error)
   {}))

(re-frame/reg-event-db
 ::show-add-apartment-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:apartment :add-apartment-dialog-open] true)))

(re-frame/reg-event-db
 ::close-add-apartment-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:apartment :add-apartment-dialog-open] false)))

(re-frame/reg-event-db
 ::assign-tenant
 [local-storage-interceptor]
 (fn [db [_ val]]
   (assoc-in db [:apartment :selected-apartment] {:action :assign-tenant :value val})))

(re-frame/reg-event-db
 ::manage-apartment
 [local-storage-interceptor]
 (fn [db [_ val]]
   (assoc-in db [:apartment :selected-apartment] {:action :manage-apartment :value val})))

(re-frame/reg-event-db
 ::cancel
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:apartment :selected-apartment] nil)))

(re-frame/reg-event-db
 ::select-tenant
 [local-storage-interceptor]
 (fn [db [_ val]]
   (assoc-in db [:apartment :selected-tenant] val)))

(re-frame.core/reg-event-fx
 ::save-selection
 (fn [{:keys [db]} _]
   (let [selected-tenant   (get-in db [:apartment :selected-tenant])
         selected-apartment (get-in db [:apartment :selected-apartment])]
     {:http-xhrio {:method          :post
                   :uri             (str config/api-url "/api/assign-tenant")
                   :params          {:apartment-id selected-apartment :tenant-id selected-tenant}
                   :format          (ajax-edn/edn-request-format)
                   :response-format (ajax-edn/edn-response-format)
                   :timeout         8000
                   :on-success      [::tenant-assigned]
                   :on-failure      [::tenant-assigning-error]}})))

(re-frame/reg-event-db
 ::tenant-assigned
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [selected-tenant   (get-in db [:apartment :selected-tenant])
         selected-apartment (get-in db [:apartment :selected-apartment])
         apartments         (get-in db [:apartment :apartments])
         updated-apartments (mapv (fn [apt]
                                    (if (= (:id apt) selected-apartment)
                                      (assoc apt :tenant selected-tenant)
                                      apt))
                                  apartments)]
     (-> db
         (assoc-in [:apartment :apartments] updated-apartments)
         (assoc-in [:apartment :selected-apartment] nil)
         (assoc-in [:apartment :selected-tenant] nil)))))

(re-frame/reg-event-fx
 ::tenant-assigning-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to assign tenant:" error)
   {}))

(re-frame/reg-event-db
 ::edit-field
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:apartment id :edit] val)))

(defn last-year []
  (let [today (js/Date.)
        current-year (.getFullYear today)]
    (- current-year 1)))

(re-frame/reg-event-fx
 ::update-data
 [local-storage-interceptor]
 (fn [{:keys [db]} [_ id val]]
   (let [parsed-val (js/parseFloat val)
         current-value (get-in db [:apartment id :value])
         apartment-id (get-in db [:apartment :selected-apartment])
         ;; still in progress 
         tenant-id ""
         data {:kind :s :year (str (last-year)) :apartment apartment-id :tenant tenant-id :value ""}] 
     (if (= parsed-val (js/parseFloat current-value))
       {:db (assoc-in db [:apartment id :edit] false)}
       {:db (-> db
                (assoc-in [:apartment id :value] val)
                (assoc-in [:apartment id :edit] false))
        :http-xhrio {:method          :post
                     :uri             (str config/api-url "/api/new-operation")
                     :params          data
                     :format          (ajax-edn/edn-request-format)
                     :response-format (ajax-edn/edn-response-format)
                     :timeout         8000
                     :on-success      [::apartment-data-updated]
                     :on-failure      [::apartment-data-update-error]}}))))

(re-frame/reg-event-db
 ::apartment-data-updated
 [local-storage-interceptor]
 (fn [db [_ response]]
   (js/console.log "Apartment attribute updated:" response)))

(re-frame/reg-event-fx
 ::apartment-data-update-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to update apartment data:" error)
   {}))