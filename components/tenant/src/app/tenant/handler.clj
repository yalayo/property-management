(ns app.tenant.handler
  (:require [app.tenant.persistance :as persistance]))

(def tenants-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-tenants) :headers {"Content-Type" "text/edn"}}))})

(def new-tenant-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-tenant (-> context :request :edn-params))
            (assoc context :response {:status 200}))})