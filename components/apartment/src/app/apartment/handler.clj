(ns app.apartment.handler
  (:require [app.apartment.persistance :as persistance]))

(defn apartments-handler [storage]
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-apartments storage) :headers {"Content-Type" "text/edn"}}))})

(def new-apartment-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-apartment (-> context :request :edn-params))
            (assoc context :response {:status 200}))})

(def assign-tenant-handler
  {:name ::post
   :enter (fn [context]
            (persistance/assign-tenant (-> context :request :edn-params))
            (assoc context :response {:status 200}))})