(ns app.rules.core
  (:require [odoyle.rules :as o]
            [clojure.spec.alpha :as s]))

(def rules
  (o/ruleset
   {}))

(def initial-session
  (-> (reduce o/add-rule (o/->session) rules)
      (o/insert ::global {})
      o/fire-rules))

(defonce *session (atom initial-session))