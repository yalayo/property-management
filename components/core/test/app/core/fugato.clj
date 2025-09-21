(ns app.core.fugato
  (:require [fugato.core :as fugato]
            [clojure.test.check.generators :as gen]
            [app.core.system :as system])
  (:import [java.time LocalDate]))
  
(def initial-state
  {:apartment-1 {:id :apartment-1 :tenant nil}
   :tenant-1 {:id :tenant-1 :name "Max Mustermann"}
   :tenant-2 {:id :tenant-2 :name "Berta Müller"}
   :ocupancy-1 nil
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
                  (gen/return :apartment-1)
                  (gen/elements [:tenant-1 :tenant-2])))

   :next-state (fn [state {[apartment tenant] :args}]
                 (-> state
                     (assoc-in [apartment :tenant] tenant)))

   :valid?     (fn [state command]
                 (println "Valid? - " command)
                 (apartment-empty? state))})

#_(defn not-ocupied-by? [state]
  (empty? (some #(when (and (= (:apartment %) :apartment-1) (= (:tenant %) :tenant-2)) %)  ;; returns first match or nil
                (:ocupancies state))))

;; Considering creating a separate spec to record the beginning of an ocupancy
(def start-ocupancy-spec
  {:run?       (fn [state] (not (apartment-empty? state)))

   :args       (fn [state]
                 (gen/tuple
                  (gen/return :apartment-1)
                  (gen/return (str (LocalDate/now)))))

   :next-state (fn [state {[apartment start-date] :args}]
                 (-> state
                     (update :occupancies
                              (fnil conj [])
                              {:id        :occupancy-1
                               :start     start-date
                               :end       nil
                               :tenant    (get-tenant state apartment)
                               :apartment apartment})))

   :valid?     (fn [state command]
                 (println "Valid? - " command)
                 (not (apartment-empty? state)))})

;; Defining the model
(def model
  {:onboarding-tenant   on-boarding-spec
   :start-ocupancy start-ocupancy-spec})

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

