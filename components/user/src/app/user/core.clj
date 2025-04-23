(ns app.user.core
  (:require [com.stuartsierra.component :as component]
            [app.user.persistance :as persistance]
            [buddy.hashers :as bh]
            [buddy.sign.jwt :as jwt]
            [clojure.string :as string]
            [app.user.database :as db]))

(defrecord UserComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn user-component [config]
  (map->UserComponent config))

(defn verify-password-and-email  
  "Verify the matching user password and new password, also check if email is registered.\n
   Args: Receive an struct like {:psw \"password\" :pswc \"new password\" :email \"email\"}\n
   Returns: an struct: {:status true :msg \"Ok\"}\n
   If status is false and email is registered the message is about email\n
   If status is false and password do not mathc but email isn't registered, the message is about password"
  [data]
  (let [result {:status true :msg "Ok"} db-email (persistance/get-account (:email data))]
   (if (and (= (:psw data) (:pswc data)) (= db-email nil))
     result
     (if (not= db-email nil)
       (assoc result :status false :msg "The email is already registered in the system")
       (assoc result :status false :msg "Passwords do not match")))))

(defn create-user [data]
  (let [{:keys [name email password password-confirmation]} data
        verify (verify-password-and-email {:psw password :pswc password-confirmation :email email})]
    (when (:status verify)
     (persistance/create-user {:name name :email email :password (bh/derive password) :created (java.util.Date.)}))))

(defn create-normal-user [email password]
  (persistance/create-user {:email email :password (bh/derive password) :created (java.util.Date.)}))

(defn create-admin-user [email password]
  (persistance/create-user {:email email :password (bh/derive password) :created (java.util.Date.) :admin true}))

(defn create-admin-test-user [email password]
  (persistance/create-user {:email email :password (bh/derive password) :created (java.util.Date.) :test true :admin true}))

(defn create-test-user [email password]
  (persistance/create-user {:email email :password (bh/derive password) :created (java.util.Date.) :test true}))

(def secret (or (System/getenv "SESSION_SECRET") "your-super-secret-key"))

;; New way of auth/authz
(defn wrap-jwt-auth [handler]
  (fn [context]
    (let [auth-header (get-in context [:request :headers "authorization"])
          token       (some-> auth-header (string/replace #"^Bearer " ""))]
      (try
        (if-let [claims (jwt/unsign token secret)]
          (handler (assoc-in context [:request :identity] claims))
          {:status 401 :body {:error "Invalid or missing token"}})
        (catch Exception _
          {:status 401 :body {:error "Invalid or missing token"}})))))

(defn sign-in [context]
  (let [params (-> context :request :edn-params)
        {:keys [user password]} params
        account (persistance/get-account user)]
    (if (and account (bh/verify password (:password account)))
      (let [claims {:email user
                    :exp   (+ (quot (System/currentTimeMillis) 1000) 86400)}  ;; Token expires in 24 hour
            token  (jwt/sign claims secret)]
        (assoc context :response {:status 200
                                  :headers {"Content-Type" "application/json"}
                                  :body {:token token}}))
      (assoc context :response
             {:status 401
              :headers {"Content-Type" "application/json"}
              :body {:error "Invalid email or password"}}))))

(comment
  "Create normal user"
  (create-normal-user "prueba@mail.com" "password")
  )