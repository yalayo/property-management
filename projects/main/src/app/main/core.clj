(ns app.main.core
  (:require
   [integrant.core :as ig]
   [com.brunobonacci.mulog :as mu]
   [app.route.interface :as route]
   [app.server.core :as server]
   [app.flags.interface :as flags]
   [app.property.interface :as property]
   [app.tenant.interface :as tenant]
   [app.apartment.interface :as apartment]
   [app.account.interface :as account]
   [app.operations.interface :as operations]
   [app.bank.interface :as bank]
   [app.html.interface :as html]
   [app.user.interface :as user]
   [app.survey.interface :as survey]
   [app.storage.interface :as storage]
   [app.core.interface :as core]
   [app.controller.interface :as controller]))

(def base-config
  {:routes
   {:external (into #{} (concat (html/get-routes)
                                (property/get-routes)
                                (tenant/get-routes)
                                (apartment/get-routes)
                                (account/get-routes)
                                (bank/get-routes)
                                (survey/get-routes)
                                (operations/get-routes)))
    :internal (into #{} (concat (user/get-internal-routes)
                                (flags/get-routes)
                                (property/get-internal-routes)
                                (bank/get-internal-routes)))
    :schema (user/get-schema)}})

(def config
  {::core/domain {:initial {}}
   ::storage/storage {:database-name "users" :schema (:schema base-config)}
   ::controller/controller {:storage (ig/ref ::storage/storage)}
   ::user/routes {:core (ig/ref ::core/domain) :controller (ig/ref ::controller/controller)}
   ::html/routes {:core (ig/ref ::core/domain) :controller (ig/ref ::controller/controller)}
   ::route/external-routes {:user-routes (ig/ref ::user/routes) :html-routes (ig/ref ::html/routes)}
   ::route/internal-routes {:routes (get-in base-config [:routes :internal])}
   ::server/server {:port 8080 :active-route :external :routes (ig/ref ::route/external-routes)}
   ::server/internal-server {:port 9090 :active-route :internal :routes (ig/ref ::route/internal-routes)}})

(defn init-logging []
  (let [prod (System/getenv "ENVIRONMENT")
        prod? (if (= prod "prod") true false)
        data {:type :multi}
        publishers [{:type :console}]
        telegram {:type :custom
                  :fqn-function "app.logs.interface/telegram-publisher"
                  :bot-token (System/getenv "BOT_TOKEN")
                  :chat-id (System/getenv "CHAT_ID")}
        config (if prod?
                 (assoc data :publishers (conj publishers telegram))
                 (assoc data :publishers publishers))]
    (mu/start-publisher! config)))

(defonce system (atom nil))

(defn start []
  (reset! system (ig/init config)))

(defn stop []
  (when @system
    (ig/halt! @system)
    (reset! system nil)))

(defn restart []
  (stop)
  (start))


(defn -main []
  (init-logging)
  (start))