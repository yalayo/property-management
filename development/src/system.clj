(ns system
  (:require [com.stuartsierra.component :as component]
            [com.brunobonacci.mulog :as mu]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]))

;; Atom to hold the system state
(def state (atom nil))

(def config {:db-spec {:dbtype "postgres"
                    :host (if (= (System/getenv "ENVIRONMENT") "prod") "prod-db" "localhost")
                    :dbname "property-management"
                    :username "user"
                    :password (if (= (System/getenv "ENVIRONMENT") "prod") "hrdata@2024" "volley@2024")
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

(defn start []
  (init-logging)
  (let [system (-> config
                   (create-system)
                   (component/start-system))]
    (reset! state system)
    (mu/log :system-started :message "Starting system for project: main")
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))

(defn stop []
  (when-let [running-system @state]
    (mu/log :system-started :message "Stopping system for project: main")
    (component/stop-system running-system)
    (reset! state nil)))

;; Optional: A `restart` function for convenience
(defn restart []
  (mu/log :system-started :message "Restarting system!")
  (stop)
  (start)
  (mu/log :system-started :message "System restarted!"))

(comment
  ;; Evaluate to start the system
  (start)

  ;; Evaluate to stop
  (stop)

  ;; Evaluate to restart
  (restart)

  )