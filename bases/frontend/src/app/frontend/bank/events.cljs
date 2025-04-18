(ns app.frontend.bank.events
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
 ::upload-data
 (fn [db [_ file]] 
   {:db (assoc-in db [:bank :transactions :is-loading] true)
    :http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/upload-transactions")
                 :headers         {"Authorization" (str "Bearer " (get-in db [:user :token]))}
                 :body             (let [form-data (js/FormData.)]
                                     (.append form-data "file" file)
                                     form-data)
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::upload-success]
                 :on-failure      [::upload-failure]}}))

(re-frame/reg-event-db
 ::upload-success
 (fn [db [_ response]]
   (js/console.log "Transactions:" response)
   (-> db
       (assoc-in [:bank :transactions] response)
       (assoc-in [:bank :transactions :is-loading] false))))

(re-frame/reg-event-fx
 ::upload-failure
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to upload file:" error)
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