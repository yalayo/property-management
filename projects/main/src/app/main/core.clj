(ns app.main.core
  (:require [com.stuartsierra.component :as component]
            [com.brunobonacci.mulog :as mu]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]
            [app.flags.interface :as flags]
            [app.property.interface :as properties]
            [app.html.interface :as html]
            [app.user.interface :as user]))

(def config {:db-spec {:dbtype "postgres"
                       :host (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_HOST") "localhost")
                       :dbname "property-management"
                       :username "user"
                       :password (System/getenv "DB_PASSWORD")
                       :dataSourceProperties {:socketTimeout 30}}
             :routes {:external (into #{} (concat (user/get-routes) (html/get-routes) (properties/get-routes)))
                      :internal (into #{} (concat (user/get-internal-routes) (flags/get-routes) (properties/get-internal-routes)))}})

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

(defn create-system [config]
  (component/system-map
   :datasource (storage/datasource-component config)
   :routes (route/route-component {:config (:routes config)})
   :server (component/using
            (server/server-component {:port 8080 :active-route :external})
            [:datasource :routes])
   :internal-server (component/using
            (server/server-component {:port 9090 :active-route :internal})
            [:datasource :routes])))

(defn -main []
  (init-logging)
  (let [system (-> config
                   (create-system)
                   (component/start-system))]
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))