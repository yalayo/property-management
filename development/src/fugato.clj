(ns fugato
  (:require [fugato.core :as fugato]
            [clojure.test.check.generators :as gen]))

(def initial-state 
  {:apartment-1 {:id :apartment-1 :tenant nil}
   :tenant {:id :tenant-1}})

(defn apartment-empty? [state]
  (not (some? (get-in state [:apartment-1 :tenant]))))

(defn get-tenant [state apartment]
  (get-in state [apartment :tenant]))

(def on-boarding-spec
  {:run?       (fn [state] (apartment-empty? state))

   :args       (fn [state]
                 (gen/return :apartment-1))

   :next-state (fn [state {apartment :args}]
                 (-> state
                     (assoc-in [apartment :tenant] :apartment-1)))

   :valid?     (fn [state {apartment :args :as command}]
                 (println "Valid? - " command)
                 (= (get-tenant state apartment)
                    (:tenant state)))})

;; Defining the model
(def model
  {:onboarding-tenant   on-boarding-spec})

;; Work on the implementation here. The system to test code. 
;; Looks like a CQRS way of doing things where C = commands.
;; Adapt my current code to work this way
(defn run [state commands]
  state)

;; Generate commands from the model
(comment
  (gen/generate (fugato/commands model initial-state))
  )

;; Something like this will probable be used to run the system test
(comment
  "Compare end result of running the same commands in my code"
  (let [commands (gen/generate (fugato/commands model initial-state))]
    (clojure.data/diff
     (fugato/execute model initial-state commands)
     (run initial-state commands)))

  ({:invoice #{:customer-a}} ;; Only in A
   {:invoice nil}            ;; Only in B
   {:customer-a #{}})        ;; In both
  )