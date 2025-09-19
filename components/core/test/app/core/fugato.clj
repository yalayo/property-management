(ns app.core.fugato
  (:require [fugato.core :as fugato]
            [clojure.test.check.generators :as gen]))
  
(def initial-state
  {:apartment-1 {:id :apartment-1 :tenant nil}
   :apartment-2 {:id :apartment-2 :tenant nil}
   :tenants [{:id :tenant-1 :name "Max Mustermann"} {:id :tenant-2 :name "Berta Müller"}]
   :contract {:id :contract-1
              :tenant-id :tenant-1
              :apartment-id :apartment-1
              :rent 800}
   :payment {:id :payment-1
             :tenant-id :tenant-1
             :month :2023-01
             :amount 800}})
  
(defn apartment-empty? [state]
  (not (some? (get-in state [:apartment-1 :tenant])))) 

(defn get-tenant [state apartment]
  (get-in state [apartment :tenant]))
  
(def on-boarding-spec
  {:run?       (fn [state] (apartment-empty? state))

   :args       (fn [state]
                 (gen/tuple
                  (gen/elements [:apartment-1 :apartment-2])
                  (gen/elements (map :id (:tenants state)))))

   :next-state (fn [state {[apartment tenant] :args}]
                 (-> state
                     (assoc-in [apartment :tenant] tenant)))

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
  
  (let [commands (gen/generate (fugato/commands model initial-state))]
    (fugato/execute model initial-state commands))
  
  ({:invoice #{:customer-a}} ;; Only in A
   {:invoice nil}            ;; Only in B
   {:customer-a #{}})        ;; In both
  )