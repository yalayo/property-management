(ns app.main.core
  (:require [com.stuartsierra.component :as component]
            [com.brunobonacci.mulog :as mu]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]))

(def config {:db-spec {:dbtype "postgres"
                       :host (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_HOST") "localhost")
                       :dbname "property-management"
                       :username "user"
                       :password (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_PASSWORD") "volley@2024")
                       :dataSourceProperties {:socketTimeout 30}}})

(defn init-logging []
  (mu/start-publisher!
   {:type :multi
    :publishers
    [{:type :console}
     {:type :custom
      :fqn-function "app.logs.interface/telegram-publisher"
      :bot-token (System/getenv "BOT_TOKEN")
      :chat-id (System/getenv "CHAT_ID")}]}))

(defn create-system [config]
  (component/system-map
   :datasource (storage/datasource-component config)
   :route (route/route-component config)
   :server (component/using
            (server/server-component config)
            [:datasource :route])))

(defn -main []
  (init-logging)
  (let [system (-> config
                   (create-system)
                   (component/start-system))]
    (mu/log :system-started :message "Starting system!")
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))