(ns rules
  (:require [odoyle.rules :as o]
            [clojure.spec.alpha :as s]))

(def rules
  (o/ruleset
   {::users
    [:what
     #_[id ::user user-id]
     [id ::user email]
     [id ::name name]
     :when (not= id ::sign-up)]
    
    ::user-sign-up
    [:what
     [::sign-up ::id id]
     [::sign-up ::user email]
     [::sign-up ::name name]
     #_#_:then
     (do
       #_(o/insert! ::users ::user user-id)
       (o/insert! id ::user email)
       (o/insert! id ::name name))]
    
    ::duplicate-email
    [:what
     [::sign-up ::user email]
     [id ::user email]
     :when (not= id ::sign-up)
     #_#_:then
     (o/insert! ::duplicate-email ::present true)]}))

;; create session and add rule
(def *session
  (atom (reduce o/add-rule (o/->session) rules)))

#_(def *session
  (atom (o/add-rule (o/->session) rules)))

(comment
  "First attempt to sign-up and if there is no duplication then send another insert user"

  (swap! *session
         (fn [session]
           (-> session
               (o/insert :user-1 ::user "user-1@mail.com")
               (o/insert :user-1 ::name "User 1")
               o/fire-rules)))
  
  (swap! *session
         (fn [session]
           (-> session
               (o/insert :user-2 ::user "user-2@mail.com")
               (o/insert :user-2 ::name "User 2")
               o/fire-rules)))
  
  (do (swap! *session
         (fn [session]
           (-> session
               (o/insert ::sign-up ::id :user-1)
               (o/insert ::sign-up ::name "User 1")
               (o/insert ::sign-up ::user "user-1@mail.com")
               o/fire-rules))) nil)
  
  
  (o/query-all @*session ::users)

  (o/query-all @*session ::duplicate-email)

  (o/query-all @*session ::user-sign-up)
  )