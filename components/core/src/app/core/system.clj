(ns app.core.system
  (:require [app.core.rules :as rules]))

(defn sign-up [state user-id email]
  (let [result (rules/process-sign-up {:id user-id :email email})
        new-state (-> state
                      (assoc-in [:users user-id] {:email email})
                      (update :emails conj email)
                      (update :user-ids conj user-id))]
    (if (some? result)
      {:state  new-state :events [{:type :persist-user :user-id user-id :email email}]}
      {:state  state :error {:type :existing-user :email email}})))

(defn onboarding-tenant [state apartment tenant]
  (let [result (rules/process-tenant-onboarding apartment tenant)]
    (assoc state apartment (first result)))) ;; Work on not returning an array instead return a map

(defn start-ocupancy [state apartment start-date]
  (let [tenant (get-in state [apartment :tenant])
        result (rules/process-start-ocupancy apartment tenant start-date)]
    (assoc-in state [:ocupancies apartment] (first result))))

(def command->fn
  {:sign-up #'sign-up
   :onboarding-tenant #'onboarding-tenant
   :start-ocupancy #'start-ocupancy})

(defn run [state commands]
  (reduce
   (fn [state {:keys [command args]}]
     (if-let [fn (get command->fn command)]
       (apply fn state args)
       (throw
        (ex-info (str "Unknown command: " command ", args:" args)
                 {:command command :arg args}))))
   state commands))

(defn init [initial]
  (let [state (atom initial)]
    ;; Optional helper for dispatching commands safely
    {:state    state
     :dispatch (fn [cmd args]
                 (swap! state
                        #(run % [{:command cmd :args args}])))
     :run      (fn [commands]
                 (swap! state run commands))}))

(defn stop [state]
  (reset! state {})
  nil)