(ns app.core.rules
  (:require [odoyle.rules :as o]))

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
     [::sign-up ::name name]]

    ::duplicate-email
    [:what
     [::sign-up ::user email]
     [id ::user email]
     :when (not= id ::sign-up)]

    ;; to register a pending onboarding of a tenant
    ::record-tenant-onboarding
    [:what
     [::onboarding-tenant ::id apartment]
     [::onboarding-tenant ::tenant tenant]
     [::onboarding-tenant ::email email]
     :then
     (println "Tenant onboarded, pending signup:" email)
     (o/insert! email ::apartment apartment)
     (o/insert! email ::tenant    tenant)
     (o/insert! email ::status    :awaiting-signup)]

    ;; query rule to read pending signups back out
    ::pending-tenant-signups
    [:what
     [email ::apartment apartment]
     [email ::tenant    tenant]
     [email ::status    :awaiting-signup]]

    ;; matches when a tenant signs up and a pending onboarding exists for the same email
    ::tenant-onboarding-match
    [:what
     [::sign-up ::id    sign-up-id]
     [::sign-up ::user  email]
     [::sign-up ::name  name]
     [email ::apartment apartment]
     [email ::tenant    tenant]
     [email ::status    :awaiting-signup]
     :then
     (o/insert! ::events ::pending
                [{:type :persist-tenant-profile
                  :data {:id        tenant
                         :email     email
                         :name      name
                         :apartment apartment}}])]

    ;; stores events emitted by other rules
    ::events
    [:what
     [::events ::pending events]]

    ::onboarding-tenant
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

(defn process-sign-up [data]
  (println "Data" data)
  (let [id (:id data)
        name (:name data)
        email (:email data)
        _ (swap! *session
                 (fn [session]
                   (-> session
                       (o/insert ::sign-up ::id id)
                       (o/insert ::sign-up ::name name)
                       (o/insert ::sign-up ::user email)
                       o/fire-rules)))
        successfull? (empty? (o/query-all @*session ::duplicate-email))]
    (when successfull?
      (swap! *session
             (fn [session]
               (-> session
                   (o/insert id ::user email)
                   (o/insert id ::name name)
                   o/fire-rules)))
      (o/query-all @*session ::user-sign-up))))

(defn process-tenant-onboarding [apartment tenant email]
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::onboarding-tenant ::id apartment)
               (o/insert ::onboarding-tenant ::tenant tenant)
               (o/insert ::onboarding-tenant ::email email)  ;; <-- add this
               o/fire-rules)))
  (o/query-all @*session ::onboarding-tenant))

(defn find-pending-signup [email]
  (->> (o/query-all @*session ::pending-tenant-signups)
       (filter #(= (:email %) email))
       first))

;; signup of a tenant after the landlord registered his/her onboarding
(defn process-tenant-signup [sign-up-id email name address]
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::sign-up ::id   sign-up-id)
               (o/insert ::sign-up ::user email)
               (o/insert ::sign-up ::name name)
               o/fire-rules)))
  (let [events (:events (first (o/query-all @*session ::events)))]  ;; <-- :events not :pending
    (swap! *session
           (fn [session]
             (-> session
                 (o/retract ::sign-up ::id)
                 (o/retract ::sign-up ::user)
                 (o/retract ::sign-up ::name)
                 (o/retract ::events ::pending)
                 o/fire-rules)))
    events))

(defn activate-tenant-signup! [email name address]
  (if-let [pending (find-pending-signup email)]
    (process-tenant-signup (:tenant pending)
                           email
                           name
                           address)
    {:error (str "No pending onboarding found for: " email)}))

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

  ;; Testing user sign-up 
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
  
  (process-tenant-onboarding :apartment-1 :tenant-1 "tenant-1@gmail.com")
  (find-pending-signup "tenant-1@gmail.com")
  (activate-tenant-signup! "tenant-1@gmail.com" "John" "Munsterstr 1")

  (o/query-all @*session ::users)
  
  (o/query-all @*session ::duplicate-email)
  
  (o/query-all @*session ::user-sign-up)
  
  )

