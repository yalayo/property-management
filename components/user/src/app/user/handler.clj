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
(def post-sign-up
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :edn-params)]
              (core/create-user params)
              (assoc context :response {:status 200})))})

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
