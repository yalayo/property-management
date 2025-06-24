(ns app.property.handler
  (:require [app.property.persistance :as persistance]
            [buddy.sign.jwt :as jwt]
            [clojure.string :as string]))

(def properties-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-properties) :headers {"Content-Type" "text/edn"}}))})

(def new-property-handler
  {:name ::post
   :enter (fn [context]
            (let [request (-> context :request)
                  data (:edn-params request)
                  token (get-in request [:headers "authorization"])
                  decoded (jwt/unsign token "env")
                  db (string/replace (:id decoded) "-" "")]
              (persistance/create-property data db))
            (assoc context :response {:status 200}))})