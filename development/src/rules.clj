(ns rules
  (:require [odoyle.rules :as o]
            [clojure.spec.alpha :as s]))

(def rules
  (o/ruleset
   {::move-player
    [:what
     [::time ::total tt]
     :then
     (o/insert! ::player ::x tt)]

    ::player
    [:what
     [::player ::x x]
     [::player ::y y]]}))

;; create session and add rule
(def *session
  (atom (reduce o/add-rule (o/->session) rules)))

(comment
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::player ::x 20)
               (o/insert ::player ::y 15)
               o/fire-rules)))
  
  (o/query-all @*session ::player)
  )