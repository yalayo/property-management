(ns app.platform-ui.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.http-fx]))

;; Initializing
;; Interceptors
(def ->local-store (after db/db->local-store))

;; Interceptor Chain
(def interceptors [->local-store])

;; To restore db from the browser's local storage
(re-frame/reg-cofx
 :local-store-db
 (fn [cofx _]
   (assoc cofx :local-store-db
          ;; read in todos from localstore, and process into a sorted map
          (into (sorted-map)
                (some->> (.getItem js/localStorage db/ls-key)
                         (cljs.reader/read-string))))))

(re-frame/reg-event-fx
 ::initialize-db
 [(re-frame/inject-cofx :local-store-db)]
 (fn-traced [{:keys [local-store-db]} _]
            {:db local-store-db
             :dispatch-n [] #_[[::property-events/get-properties]
                               [::tenant-events/get-tenants]
                               [::apartment-events/get-apartments]
                               [::account-events/get-accounts]]}))

(re-frame/reg-event-fx
 :init-google
 (fn [_ _]
   {:init-google-auth true}))

(defn handle-credential-response [response]
  (let [jwt-token (.-credential response)]
    (re-frame/dispatch [:auth-success jwt-token])))

(re-frame/reg-fx
 :init-google-auth
 (fn []
   (js/google.accounts.id.initialize
    (clj->js {:client_id "964100976552-iehqptnto63ait3j18985cai9u2jgml2.apps.googleusercontent.com"
              :callback handle-credential-response}))

   ;; Render button (optional)
   (js/google.accounts.id.renderButton
    (.getElementById js/document "g-signin")
    (clj->js {:theme "outline" :size "large"}))

   ;; Or enable One Tap
   (js/google.accounts.id.prompt)))

(re-frame/reg-event-db
 :auth-success
 (fn [db [_ jwt-token]]
   (assoc db :auth {:jwt jwt-token})))