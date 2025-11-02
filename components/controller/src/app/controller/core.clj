(ns app.controller.core
  (:require [buddy.hashers :as bh]))

#_{:conn conn
 :transact transact
 :query query
 :query-with-parameter query-with-parameter}

(defn save-user! [storage data]
  (println "Save: " data)
  (let [conn (:conn storage)
        transact (:transact storage)]
    #_(transact {:email email :password (bh/derive password) :created (java.util.Date.)})))


(defn persist! [events storage]
  (doseq [e events]
    (case (:type e)
      :persist-user (save-user! storage (:data e))
      nil)))

(defn init [storage]
  {:dispatch (fn [events]
               (persist! events storage))})