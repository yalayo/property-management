(ns app.frontend.account.events
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
   (assoc-in db [:account :form id] val)))

(re-frame/reg-event-fx
 ::save-account
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/api/new-account")
                 :params          (get-in db [:account :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::account-submitted]
                 :on-failure      [::account-creation-error]}}))

(re-frame/reg-event-db
 ::account-submitted
 [local-storage-interceptor]
 (fn [db [_ response]]
   (let [accounts (get-in db [:account :accounts])
         account (get-in db [:account :form])
         updated (conj accounts account)]
     (js/console.log "Account saved:" response)
     (-> db
         (assoc-in [:account :accounts] updated)
         (assoc-in [:account :form] nil)
         (assoc-in [:account :add-account-dialog-open] false)))))

(re-frame/reg-event-fx
 ::account-creation-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt account:" error)
   {}))

(re-frame/reg-event-fx
 ::get-accounts
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (str config/api-url "/api/accounts")
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::update-db]
                 :on-failure      [::get-accounts-error]}}))

(re-frame/reg-event-db
 ::update-db
 [local-storage-interceptor]
 (fn [db [_ response]]
   (assoc-in db [:account :accounts] response)))

(re-frame/reg-event-fx
 ::get-accounts-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to get the list of accounts:" error)
   {}))

(re-frame/reg-event-db
 ::show-add-account-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:account :add-account-dialog-open] true)))

(re-frame/reg-event-db
 ::close-add-account-dialog
 [local-storage-interceptor]
 (fn [db]
   (assoc-in db [:account :add-account-dialog-open] false)))