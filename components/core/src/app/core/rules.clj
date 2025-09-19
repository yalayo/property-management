(ns app.core.rules
  (:require [odoyle.rules :as o]))

(def rules
  (o/ruleset
   {::onboarding-tenant
    [:what
     [::onboarding-tenant ::id id]
     [::onboarding-tenant ::tenant tenant]]}))

;; create session and add rule
(def *session
  (atom (reduce o/add-rule (o/->session) rules)))

(defn process-tenant-onboarding [apartment tenant]
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::onboarding-tenant ::id apartment)
               (o/insert ::onboarding-tenant ::tenant tenant)
               o/fire-rules)))
  (o/query-all @*session ::onboarding-tenant))

(comment
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::onboarding-tenant ::id :apartment-1)
               (o/insert ::onboarding-tenant ::tenant :tenant-1)
               o/fire-rules)))

  (o/query-all @*session ::onboarding-tenant)
  
  )

