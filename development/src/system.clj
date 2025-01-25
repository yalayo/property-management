(ns system
  (:require [com.stuartsierra.component :as component]
            [com.brunobonacci.mulog :as mu]
            [app.storage.interface :as storage]
            [app.route.interface :as route]
            [app.server.core :as server]
            [app.html.interface :as html]
            [app.user.interface :as user]))

;; Atom to hold the system state
(def state (atom nil))

(def config {:db-spec {:dbtype "postgres"
                       :host (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_HOST") "localhost")
                       :dbname "property-management"
                       :username "user"
                       :password (if (= (System/getenv "ENVIRONMENT") "prod") (System/getenv "DB_PASSWORD") "volley@2024")
                       :dataSourceProperties {:socketTimeout 30}}
             :routes {:external (into #{} (concat (user/get-routes) (html/get-routes)))
                      :internal {}}})

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
   :route (route/route-component {:config (get-in config [:routes :external])})
   :internal-routes (route/route-component {:config (get-in config [:routes :external])})
   :server (component/using
            (server/server-component {:port 8080})
            [:datasource :route])
   :internal-server (component/using
            (server/server-component {:port 9090})
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
    (mu/log :system-stoped :message "Stopping system for project: main")
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