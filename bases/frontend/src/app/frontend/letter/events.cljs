(ns app.frontend.letter.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [app.frontend.events :as main-events]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]
            [ajax.core :as ajax]
            [ajax.core :refer [raw-response-format]]))

(def local-storage-interceptor main-events/->local-store)

(re-frame/reg-event-db
 ::update-field
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:property :form id] val)))

(re-frame/reg-event-fx
 ::upload-data
 (fn [db [_ file]] 
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/upload-details")
                 :headers         {"Authorization" (str "Bearer " (get-in db [:user :token]))}
                 :body             (let [form-data (js/FormData.)]
                                     (.append form-data "file" file)
                                     form-data)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::upload-success]
                 :on-failure      [::upload-failure]}}))

(re-frame/reg-event-db
 ::upload-success
 [local-storage-interceptor]
 (fn [db [_ response]]
   (js/console.log "Letters data:" response)
   (-> db
       (assoc-in [:letter :tenants] response)
       (assoc-in [:letter :data :is-loading] false))))

(re-frame/reg-event-fx
 ::upload-failure
 (fn [{:keys [_]} [_ {:keys [status status-text response]}]]
   (js/console.error (str "Upload failed! HTTP status: " status " " status-text))
   (js/console.log "Backend responded with:" (clj->js response))
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

(re-frame/reg-event-fx
 ::create-letter
 (fn [{:keys [db]} [_ data]]
   (let [info (js->clj data :keywordize-keys true)
         year (:year info)
         id (:id info)
         tenants (get-in db [:letter :tenants])
         tenant-data (first (filter #(= (:property-id %) id) tenants))]
     {:http-xhrio {:method          :post
                   :uri             (str config/api-url "/api/create-letter")
                   :headers         {"Authorization" (str "Bearer " (get-in db [:user :token]))}
                   :params          {:data tenant-data :year year}
                   :format          (ajax-edn/edn-request-format)
                   :response-format (raw-response-format)
                   :timeout         8000
                   :on-success      [::create-letter-success]
                   :on-failure      [::create-letter-failure]}})))

(re-frame/reg-event-db
 ::create-letter-success
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [blob (js/Blob. #js [response] #js {:type "application/pdf"})
         url (.createObjectURL js/URL blob)]
     (.open js/window url "_blank")
     (-> db
         (assoc-in [:letter :tenants] nil)
         (assoc-in [:letter :data :is-loading] false)))))

(re-frame/reg-event-fx
 ::create-letter-failure
 (fn [{:keys [_]} [_ {:keys [status status-text response]}]]
   (js/console.error (str "Letter creation failed: " status " " status-text))
   (js/console.log "Backend responded with:" (clj->js response))
   {}))