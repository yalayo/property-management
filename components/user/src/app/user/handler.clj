(ns app.user.handler
  (:require [app.user.database :as db]
            [app.user.core :as core]))

(def post-sign-in
  {:name ::post
   :enter (fn [context]
            (let [session (-> context :requet :session)]
              (if (empty? session)
                (core/sign-in context)
                (assoc context :response {:status 200
                                          :headers {"HX-Location" "/upload-excel"}}))))})
(defn post-sign-up [core controller]
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :edn-params)
                  domain (:dispatch core)
                  process-events (:dispatch controller)
                  result (domain :sign-up [(str (java.util.UUID/randomUUID)) (:user params)])
                  error (:error result)
                  events (:events result)]
              (if (some? error)
                (assoc context :response {:status 500 :body error})
                (assoc context :response {:status 200 :body (process-events events)}))))})

(def post-change-password
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :params)
                  {:keys [email password new-password]} params
                  verify (core/verify-password-and-email {:psw password :email email})]
              (if (= (:status verify) true)
                (do
                  (db/change-password email new-password)
                  (assoc context :response {:status 200
                                            :headers {"HX-Redirect" "/flags"}}))
                #_(assoc context :response (-> (change-password-form {:error (:msg verify) :email email}) (ok))))))})
