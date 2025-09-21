(ns app.core.rules
  (:require [odoyle.rules :as o]))

(def rules
  (o/ruleset
   {::onboarding-tenant
    [:what
     [::onboarding-tenant ::id id]
     [::onboarding-tenant ::tenant tenant]]
    
    ::start-ocupancy
    [:what
     [::start-ocupancy ::start start]
     [::start-ocupancy ::tenant tenant]
     [::start-ocupancy ::apartment apartment]]}))

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

(defn process-start-ocupancy [apartment tenant start-date]
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::start-ocupancy ::start start-date)
               (o/insert ::start-ocupancy ::tenant tenant)
               (o/insert ::start-ocupancy ::apartment apartment)
               o/fire-rules)))
  (o/query-all @*session ::start-ocupancy))

(comment
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::start-ocupancy ::start "2025-09-21")
               (o/insert ::start-ocupancy ::tenant :tenant-1)
               (o/insert ::start-ocupancy ::apartment :apartment-1)
               o/fire-rules)))

  (o/query-all @*session ::start-ocupancy)
  
  )

