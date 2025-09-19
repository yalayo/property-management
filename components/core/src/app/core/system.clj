(ns app.core.system)

(defn onboarding-tenant [state apartment tenant]
  (println "System log: " apartment)
  (println "System log: " tenant))

(def command->fn
  {:onboarding-tenant #'onboarding-tenant})

(defn run [state commands]
  (reduce
   (fn [state {:keys [command args]}]
     (if-let [fn (get command->fn command)]
       (apply fn state args)
       (throw
        (ex-info (str "Unknown command: " command ", args:" args)
                 {:command command :arg args}))))
   state commands))