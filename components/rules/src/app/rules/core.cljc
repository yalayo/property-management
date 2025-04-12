(ns app.rules.core
  (:require [odoyle.rules :as o]
            [clojure.spec.alpha :as s]))

(def rules
  (o/ruleset
   {::get-all-accounts
    [:what
     [::global ::all-accounts all-accounts]]}))

(def initial-session
  (-> (reduce o/add-rule (o/->session) rules)
      (o/insert ::global {::all-accounts []})
      o/fire-rules))

(defonce *session (atom initial-session))

(defn insert-accounts [session accounts]
  (->> accounts
       (reduce
        (fn [session {:keys [id text done]}]
          (o/insert session id {::text text ::done done}))
        session)
       o/fire-rules))

(defn get-all-accounts [session]
  (-> (o/query-all session ::get-all-accounts)
      first
      :all-accounts))

(comment
  (o/query-all *session ::get-all-accounts)
  (get-all-accounts *session)
  )