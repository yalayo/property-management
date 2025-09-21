(ns app.core.system
  (:require [app.core.rules :as rules]))

(defn onboarding-tenant [state apartment tenant]
  (let [result (rules/process-tenant-onboarding apartment tenant)]
    (assoc state apartment (first result)))) ;; Work on not returning an array instead return a map

(defn start-ocupancy [state apartment start-date]
  (let [tenant (get-in state [apartment :tenant])
        result (rules/process-start-ocupancy apartment tenant start-date)]
    (assoc-in state [:ocupancies apartment] (first result))))

(def command->fn
  {:onboarding-tenant #'onboarding-tenant
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