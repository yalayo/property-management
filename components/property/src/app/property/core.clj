(ns app.property.core
  (:require [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.property.list :as properties]))

(def properties-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Properties" :content (properties/content [])}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Properties"))))})

(def new-property-handler
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :params)
                  property-name (:name params)]
              (assoc context :response (properties/property-info {:name property-name}))))})

(def routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]})

(def internal-routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]
    ["/new-property"
     :post new-property-handler
     :route-name ::new-property]})