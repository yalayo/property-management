(ns app.controller.core
  (:require [buddy.hashers :as bh]))

(defn save-user! [storage data]
  (println "Save: " data)
  (let [conn (:conn storage)
        transact (:transact storage)
        tx [{:id (:id data)
             :email (:email data)
             :password (bh/derive (:password data))
             :created (java.util.Date.)}]
        tx-result (transact tx)] ;; <--- deref here!
    (println "Transaction result:" tx-result)))


(defn persist! [events storage]
  (doseq [e events]
    (case (:type e)
      :persist-user (save-user! storage (:data e))
      nil)))

(defn init [storage]
  {:dispatch (fn [events]
               (persist! events storage))})