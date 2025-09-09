(ns app.frontend.user.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [app.frontend.events :as main-events]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(def local-storage-interceptor main-events/->local-store)

(re-frame/reg-event-db
 ::update-sign-in
 [local-storage-interceptor]
 (fn [db [_ id val]]
   (assoc-in db [:user :sign-in :form id] val)))

(re-frame/reg-event-fx
 ::sign-in
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/sign-in")
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
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/sign-up")
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