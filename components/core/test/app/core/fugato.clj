(ns app.core.fugato
  (:require [fugato.core :as fugato]
            [clojure.test.check.generators :as gen]
            [app.core.system :as system])
  (:import [java.time LocalDate]))
  
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

(defn not-ocupied-by? [state]
  (empty? (some #(when (and (= (:apartment %) :apartment-2) (= (:tenant %) :tenant-2)) %)  ;; returns first match or nil
                (:ocupancies state))))

;; Considering creating a separate spec to record the beginning of an ocupancy
(def start-ocupancy-spec
  {:run?       (fn [state] (and (apartment-empty? state) (not-ocupied-by? state)))

   :args       (fn [state]
                 (gen/tuple
                  (gen/elements [:apartment-1 :apartment-2])
                  (gen/elements (map :id (:tenants state)))
                  (gen/return (str (LocalDate/now)))))

   :next-state (fn [state {[apartment tenant start-date] :args}]
                 (-> state
                     (update :occupancies
                              (fnil conj [])
                              {:id        :occupancy-1
                               :start     start-date
                               :end       nil
                               :tenant    tenant
                               :apartment apartment})))

   :valid?     (fn [state {apartment :args :as command}]
                 (println "Valid? - " command)
                 (= (get-tenant state apartment)
                    (:tenant state)))})

;; Defining the model
(def model
  {:onboarding-tenant   on-boarding-spec
   :start-ocupancy-spec start-ocupancy-spec})

;; Generate commands from the model
(comment
  (gen/generate (fugato/commands model initial-state))
  )

;; Something like this will probable be used to run the system test
(comment
  "Compare end result of running the same commands in my code"
  (let [commands (gen/generate (fugato/commands model initial-state))]
    (println "Commands: " commands)
    (clojure.data/diff
     (fugato/execute model initial-state commands)
     (system/run initial-state commands)))
  
  (let [commands (gen/generate (fugato/commands model initial-state))]
    (println "Command: " commands)
    (fugato/execute model initial-state commands))
  
  ({:invoice #{:customer-a}} ;; Only in A
   {:invoice nil}            ;; Only in B
   {:customer-a #{}})        ;; In both
  )

#_({:command :start-ocupancy-spec, :args [:apartment-2 :tenant-1 2025-09-20]} 
 {:command :onboarding-tenant, :args [:apartment-2 :tenant-1]} 
 {:command :start-ocupancy-spec, :args [:apartment-2 :tenant-2 2025-09-20]})