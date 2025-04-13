(ns app.frontend.survey.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [app.frontend.db :as db]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(re-frame/reg-event-db
 ::answer-question
 (fn [db [_ val]]
   (let [index (get-in db [:survey :current-question-index])
         id (keyword (str index))]
     (assoc-in db [:survey :responses id] val))))

(re-frame/reg-event-db
 ::next-question
 (fn [db]
   (let [index (get-in db [:survey :current-question-index])
         total (count (get-in db [:survey :questions]))]
     (if (>= index (dec total))
       (-> db
           (assoc-in [:survey :show-email-form] true)
           (assoc-in [:survey :current-question-index] (inc index)))
       (assoc-in db [:survey :current-question-index] (inc index))))))

(re-frame/reg-event-db
 ::previous-question
 (fn [db]
   (let [index (get-in db [:survey :current-question-index])
         show-email-form? (get-in db [:survey :show-email-form])]
     (when (pos? index)
       (if (and (<= index 20) show-email-form?)
         (-> db
             (assoc-in [:survey :show-email-form] false)
             (assoc-in [:survey :current-question-index] (dec index)))
         (assoc-in db [:survey :current-question-index] (dec index)))))))

(re-frame/reg-event-db
 ::update-email-form
 (fn [db [_ id val]]
   (assoc-in db [:survey :form id] val)))

(re-frame/reg-event-fx
 ::save-survey
 (fn [{:keys [db]} _]
   (let [survey-data {:responses (get-in db [:survey :responses])
                      :email (get-in db [:survey :form :email])}]
     {:http-xhrio {:method          :post
                   :uri             (str config/api-url "/api/survey")
                   :params          survey-data
                   :format          (ajax-edn/edn-request-format)
                   :response-format (ajax-edn/edn-response-format)
                   :timeout         8000
                   :on-success      [::survey-submitted]
                   :on-failure      [::survey-submitt-error]}})))

(re-frame/reg-event-db
 ::survey-submitted
 (fn [db [_ response]]
   (js/console.log "Survey summited:" response)))

(re-frame/reg-event-fx
 ::handle-init-db-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt survey:" error)
   {}))