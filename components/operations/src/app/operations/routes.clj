(ns app.operations.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.operations.handler :as handler]))

(def routes
  #{["/api/operations"
     :get handler/operations-handler
     :route-name ::operations]
    ["/api/new-operation"
     :post [(body-params/body-params) params/keyword-params handler/new-operation-handler]
     :route-name ::new-operation]})