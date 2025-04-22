(ns app.account.handler
  (:require [app.account.persistance :as persistance]))

(def accounts-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-accounts) :headers {"Content-Type" "text/edn"}}))})

(def new-account-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-account (-> context :request :edn-params))
            (assoc context :response {:status 200}))})