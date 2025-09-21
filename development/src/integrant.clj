(ns integrant
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
   [app.survey.interface :as survey]))

(def base-config
  {:db-spec {:dbtype "postgres"
             :host (if (= (System/getenv "ENVIRONMENT") "prod")
                     (System/getenv "DB_HOST")
                     "localhost")
             :dbname "property-management"
             :username "user"
             :password (System/getenv "DB_PASSWORD")
             :dataSourceProperties {:socketTimeout 30}}

   :routes
   {:external (into #{} (concat (user/get-routes)
                                (html/get-routes)
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
                                (bank/get-internal-routes)))}})

(def config
  ;; Integrant system configuration
  {::logging {}
   ::user {:config base-config}
   ::survey {:config base-config}
   ::property {:config base-config}
   ::tenant {:config base-config}
   ::apartment {:config base-config}
   ::account {:config base-config}
   ::operations {:config base-config}
   ::routes {:config (:routes base-config)}
   ::server {:port 8080 :active-route :external :routes (ig/ref ::routes)}
   ::internal-server {:port 9090 :active-route :internal :routes (ig/ref ::routes)}})

;; Logging
(defmethod ig/init-key ::logging [_ _]
  (let [prod? (= (System/getenv "ENVIRONMENT") "prod")
        publishers [{:type :console}]
        telegram   {:type :custom
                    :fqn-function "app.logs.interface/telegram-publisher"
                    :bot-token (System/getenv "BOT_TOKEN")
                    :chat-id  (System/getenv "CHAT_ID")}
        cfg (if prod?
              {:type :multi :publishers (conj publishers telegram)}
              {:type :multi :publishers publishers})]
    (mu/start-publisher! cfg)))

(defmethod ig/halt-key! ::logging [_ publisher]
  #_(when publisher (mu/stop-publisher! publisher)))

;; Example: user
(defmethod ig/init-key ::user [_ {:keys [config]}]
  (user/user-component config))

(defmethod ig/halt-key! ::user [_ c]
  #_(component/stop c)) ; if user-component returns a component-like object

;; Repeat similar for ::survey, ::property, etc.

;; Routes
(defmethod ig/init-key ::routes [_ {:keys [config]}]
  (route/route-component {:config config}))

(defmethod ig/halt-key! ::routes [_ r]
  )

;; Server
(defmethod ig/init-key ::server [_ {:keys [port active-route routes]}]
  (server/server-component {:port port :active-route active-route})
  {:routes routes})

(defmethod ig/halt-key! ::server [_ srv]
  )

(defmethod ig/init-key ::internal-server [_ {:keys [port active-route routes]}]
  (server/server-component {:port port :active-route active-route})
  {:routes routes})

(defmethod ig/halt-key! ::internal-server [_ srv]
  )

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

(comment
  "System management"
  (start)
  (stop)
  (restart)
  )