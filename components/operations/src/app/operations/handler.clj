(ns app.operations.handler
  (:require [app.operations.core :as core]
            [app.operations.persistance :as persistance]))

(def operations-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-operations) :headers {"Content-Type" "text/edn"}}))})

(def new-operation-handler
  {:name ::post
   :enter (fn [context]
            (core/store-operation (-> context :request :edn-params))
            (assoc context :response {:status 200}))})