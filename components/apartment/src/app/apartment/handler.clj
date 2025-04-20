(ns app.apartment.handler
  (:require [app.apartment.persistance :as persistance]))

(def apartments-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-apartments) :headers {"Content-Type" "text/edn"}}))})

(def new-apartment-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-apartment (-> context :request :edn-params))
            (assoc context :response {:status 200}))})