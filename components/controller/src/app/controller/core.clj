(ns app.controller.core)

(defn save-user! [storage data]
  (println "Save: " data))


(defn persist! [events storage]
  (doseq [e events]
    (case (:type e)
      :persist-user (save-user! storage (:data e))
      nil)))

(defn init [storage]
  {:dispatch (fn [events]
               (persist! events storage))})