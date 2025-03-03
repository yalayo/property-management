(ns app.property.core
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.property.list :as properties]
            [app.property.persistance :as persistance]
            [app.property.new-property :as new-property]))

(def properties-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Properties" :content (properties/content (persistance/list-properties))}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Properties"))))})

(def new-property-handler
  {:name ::post
   :enter (fn [context]
            (println "Test")
            (let [params (-> context :request :params)
                  {:keys [name]} params]
              (println "Property name: " name)
              (persistance/create-property name)
              (assoc context :response (html/respond new-property/get-new-property-form "New property"))))})

(def routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]})

(def internal-routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]
    ["/new-property"
     :post [(body-params/body-params) params/keyword-params new-property-handler]
     :route-name ::new-property]})