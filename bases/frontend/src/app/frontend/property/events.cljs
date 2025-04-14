(ns app.frontend.property.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.config :as config]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax-edn]))

(re-frame/reg-event-db
 ::update-field
 (fn [db [_ id val]]
   (assoc-in db [:property :form id] val)))

(re-frame/reg-event-fx
 ::save-property
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (str config/api-url "/new-property")
                 :params          (get-in db [:property :form])
                 :format          (ajax-edn/edn-request-format)
                 :response-format (ajax-edn/edn-response-format)
                 :timeout         8000
                 :on-success      [::property-submitted]
                 :on-failure      [::property-creation-error]}}))

(re-frame/reg-event-db
 ::property-submitted
 (fn [db [_ response]]
   (js/console.log "Property summited:" response)))

(re-frame/reg-event-fx
 ::property-creation-error
 (fn [{:keys [_]} [_ error]]
   (js/console.error "Failed to submitt property:" error)
   {}))