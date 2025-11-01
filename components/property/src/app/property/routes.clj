(ns app.property.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.property.handler :as handler]))

(def routes
  #{["/api/properties"
     :get handler/properties-handler
     :route-name ::properties]
    ["/api/new-property"
     :post [(body-params/body-params) params/keyword-params handler/new-property-handler]
     :route-name ::new-property]})

(defn get-routes [storage]
  #{["/api/properties"
     :get (handler/properties-handler storage)
     :route-name ::properties]
    ["/api/new-property"
     :post [(body-params/body-params) params/keyword-params handler/new-property-handler]
     :route-name ::new-property]})