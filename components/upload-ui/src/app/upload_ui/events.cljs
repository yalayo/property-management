(ns app.upload-ui.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.upload-ui.db :as db]
            [app.frontend.config :as config]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(def local-storage-interceptor (after (after db/db->local-store)))

(re-frame/reg-event-db
 ::update-email
 [local-storage-interceptor]
 (fn [db [_ value]]
   (assoc-in db [:upload :data :email] value)))

(re-frame/reg-event-fx
 ::upload-data
 (fn [db [_ file]]
   {:dispatch [::reset-letter]
    :http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/upload-details")
                 :headers         {"Authorization" (str "Bearer " (get-in db [:user :token]))}
                 :body             (let [form-data (js/FormData.)
                                         email (get-in db [:upload :data :email])]
                                     (.append form-data "file" file)
                                     (.append form-data "email" email)
                                     form-data)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::upload-success]
                 :on-failure      [::upload-failure]}}))

(re-frame/reg-event-db
 ::upload-success
 [local-storage-interceptor]
 (fn [db [_ response]]
   (js/console.log "Upload response:" response)
   (-> db
       (assoc-in [:upload :data :response] response)
       (assoc-in [:upload :data :is-loading] false))))

(re-frame/reg-event-fx
 ::upload-failure
 [local-storage-interceptor]
 (fn [{:keys [db]} [_ {:keys [_ _ response]}]]
   {:db (-> db
            (assoc-in [:upload :data :errors] response)
            (assoc-in [:upload :data :is-loading] false))}))

(re-frame/reg-event-db
 ::update-sign-in
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:user :sign-in :form id] val)))

(re-frame/reg-event-db
 ::update-sign-up
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:user :sign-up :form id] val)))

(re-frame/reg-event-fx
 ::sign-in
 (fn [{:keys [db]} config]
   (println "Config: " config)
   {:http-xhrio {:method          :post
                 :uri             (str (:api-url config) "/api/sign-in")
                 :params          (get-in db [:user :sign-in :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::signed-in]
                 :on-failure      [::sign-in-error]}}))

(re-frame/reg-event-db
 ::signed-in
 [local-storage-interceptor]
 (fn [db [_ response]]
   (-> db
       (assoc-in [:user :token] response)
       (assoc-in [:user :user-loged-in?] true)
       (assoc-in [:user :sign-in :form] nil))))

(re-frame/reg-event-fx
 ::sign-in-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Signin failed:" error)
   {}))

(re-frame/reg-event-db
 ::update-sign-up
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:user :sign-up :form id] val)))

(re-frame/reg-event-fx
 ::sign-up
 (fn [{:keys [db]} config]
   {:http-xhrio {:method          :post
                 :uri             (str (:api-url config) "/api/sign-up")
                 :params          (get-in db [:user :sign-up :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::signed-up]
                 :on-failure      [::sign-up-error]}}))

(re-frame/reg-event-db
 ::signed-up
 [local-storage-interceptor]
 (fn [db [_ response]]
   (assoc-in db [:user :token] response)))

(re-frame/reg-event-fx
 ::sign-up-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Signup failed:" error)
   {}))

(re-frame/reg-event-db
 ::show-sign-up
 [local-storage-interceptor]
 (fn [db [_ _]]
   (assoc-in db [:user :active-form] :sign-up)))