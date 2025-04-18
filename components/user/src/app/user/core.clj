(ns app.user.core
  (:require [buddy.hashers :as bh]
            [buddy.sign.jwt :as jwt]
            [clojure.string :as string]
            [app.user.database :as db]))

(defn verify-password-and-email  
  "Verify the matching user password and new password, also check if email is registered.\n
   Args: Receive an struct like {:psw \"password\" :pswc \"new password\" :email \"email\"}\n
   Returns: an struct: {:status true :msg \"Ok\"}\n
   If status is false and email is registered the message is about email\n
   If status is false and password do not mathc but email isn't registered, the message is about password"
  [data]
  (let [result {:status true :msg "Ok"} db-email (db/get-account (:email data))]
   (if (and (= (:psw data) (:pswc data)) (= db-email nil))
     result
     (if (not= db-email nil)
       (assoc result :status false :msg "The email is already registered in the system")
       (assoc result :status false :msg "Passwords do not match")))))

(defn verifyPassw [data]
  (let [result {:status true :msg "Ok"} db-email (db/get-account (:email data))]
    (if (and (bh/check (:psw data) (:password db-email)) db-email)
      result
        (assoc result :status false :msg "Wrong email or password"))))

(def secret "your-super-secret-key")  ;; Use an env var!

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
        account (db/get-account user)]
    (if (and account (bh/verify password (:password account)))
      (let [claims {:email user
                    :role  (:role account)
                    :exp   (+ (quot (System/currentTimeMillis) 1000) 3600)}  ;; Token expires in 1 hour
            token  (jwt/sign claims secret)]
        (assoc context :response {:status 200
                                  :headers {"Content-Type" "application/json"}
                                  :body {:token token}}))
      (assoc context :response
             {:status 401
              :headers {"Content-Type" "application/json"}
              :body {:error "Invalid email or password"}})
      #_(assoc context :response {:status 200
                                :headers {"HX-Location" "/upload-excel"}
                                :session (select-keys (into {} account) [:email :created-at])})
      #_(assoc context :response (-> (sign-in-form {:error "Passwords are not matching" :email email}) (ok))))))