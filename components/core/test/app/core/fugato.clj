(ns app.core.fugato
  (:require [fugato.core :as fugato]
            [clojure.test.check.generators :as gen]
            [app.core.system :as system])
  (:import [java.time LocalDate]))
  
(def initial-state
  {:apartment-1 {:id :apartment-1 :tenant nil}
   :tenant-1 {:id :tenant-1 :name "Max Mustermann"}
   :tenant-2 {:id :tenant-2 :name "Berta Müller"}
   :ocupancies nil
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

(defn not-ocupied? [state]
  (not (some? (get-in state [:ocupancies :apartment-1]))))

;; Considering creating a separate spec to record the beginning of an ocupancy
(def start-ocupancy-spec
  {:run?       (fn [state] (and (not (apartment-empty? state)) (not-ocupied? state)))

   :args       (fn [state]
                 (gen/tuple
                  (gen/return :apartment-1)
                  (gen/return (str (LocalDate/now)))))

   :next-state (fn [state {[apartment start-date] :args}]
                 (-> state
                     (assoc-in [:ocupancies apartment] {:start     start-date
                                                        :tenant    (get-tenant state apartment)
                                                        :apartment apartment})))

   :valid?     (fn [state command]
                 (println "Valid? - " command)
                 (and (not (apartment-empty? state)) (not-ocupied? state)))})

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

;; User management system test
(def initial-state-users
  {:users {}         ;; map of user-id → user-data
   :emails #{}
   :user-ids #{}})     ;; a set of all used emails to check uniqueness

(defn email-available? [state email]
  (not (contains? (:emails state) email)))

(defn user-available? [state user-id]
  (not (contains? (:user-ids state) user-id)))

(def sign-up-spec
  {:run?       (fn [state]
                 ;; Only run if at least one free user-id and one free email exist 
                 (and (not-empty (remove (:user-ids state) [:user-1 :user-2 :user-3]))
                      (not-empty (remove (:emails   state) ["email-1@mail.com"
                                                            "email-2@mail.com"
                                                            "email-3@mail.com"]))))

   :args       (fn [state]
                 ;; Pick only from the *available* ids/emails
                 (let [free-users (vec (remove (:user-ids state)
                                               [:user-1 :user-2 :user-3]))
                       free-mails (vec (remove (:emails state)
                                               ["email-1@mail.com"
                                                "email-2@mail.com"
                                                "email-3@mail.com"]))]
                   (gen/tuple (gen/elements free-users)
                              (gen/elements free-mails))))

   :next-state (fn [state {[user-id email] :args}]
                 (-> state
                      (assoc-in [:users user-id] {:email email})
                      (update :emails conj email)
                      (update :user-ids conj user-id)))   ;; <- mark user-id as used


   :valid?     (fn [state {[user-id email] :args}]
                 (and (user-available? state user-id)
                      (email-available? state email)))})


(def users-model
  {:sign-up sign-up-spec})

(comment
  "Compare end result of running the same commands in my code"
  (let [commands (gen/generate (fugato/commands users-model initial-state-users))]
    (println "Commands: " commands)
    (clojure.data/diff
     (fugato/execute users-model initial-state-users commands)
     (system/run initial-state-users commands)))

  (let [commands (gen/generate (fugato/commands users-model initial-state-users))]
    (println "Command: " commands)
    (fugato/execute users-model initial-state-users commands))

  )