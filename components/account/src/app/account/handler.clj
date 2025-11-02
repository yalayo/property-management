(ns app.account.handler
  (:require [app.account.persistance :as persistance]))

(defn accounts-handler [storage]
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-accounts storage) :headers {"Content-Type" "text/edn"}}))})

(def new-account-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-account (-> context :request :edn-params))
            (assoc context :response {:status 200}))})