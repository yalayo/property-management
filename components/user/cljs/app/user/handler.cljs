(ns app.user.handler
  (:require
   ["jsonwebtoken" :as jwt]
   [app.user.persistance :as persistance]
   [app.worker.async :refer [js-await]]
   [app.worker.cf :as cf]))

(def secret "MY_SUPER_SECRET")   ;; store in env later

(defn passwords-match? [plain stored hashed-fn]
  (js-await [hashed (hashed-fn plain "temporary salt")]
            (= hashed stored)))

(defn post-sign-in [_ request _ _]
  (js-await [data (cf/request->edn request)]
            (let [{:keys [user password]} data]
              (js-await [account (persistance/get-account user)]
                        (if (nil? account)
                          (cf/response-edn {:error "Invalid email or password"} {:status 401})
                          (js-await [ok? (passwords-match? password (:password account) persistance/hash-password)]
                                    (if-not ok?
                                      (cf/response-edn {:error "Invalid email or password"} {:status 401})
                                      
                                      ;; password matches → create JWT
                                      (let [claims #js {:email user
                                                        :exp (+ (js/Math.floor (/ (.now js/Date) 1000))
                                                                86400)}      ;; 24h
                                            token (jwt/sign claims secret)]
                                        (cf/response-edn {:token token} {:status 200})))))))))

(defn post-sign-up [_ request _ _]
  (js-await [data (cf/request->edn request)]
            (let [{:keys [user name password]} data]
              (persistance/create-account user password)
              (cf/response-edn {:created true} {:status 201}))))
