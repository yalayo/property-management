(ns app.frontend.dashboard.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [app.frontend.events :as main-events]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(def local-storage-interceptor main-events/->local-store)

(re-frame/reg-event-db
 ::change-active-tab
 [local-storage-interceptor] 
 (fn [db [_ id _]]
   (js/console.log "Id: " id)
   (assoc-in db [:dashboard :active-tab] id)))

(re-frame/reg-event-fx
 ::log-out
 [local-storage-interceptor]
 (fn [{:keys [db]} _]
   {:db (-> db
            (assoc-in [:user :token] nil)
            (assoc-in [:user :user-loged-in?] false))})) 


