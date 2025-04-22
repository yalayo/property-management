(ns app.apartment.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.apartment.handler :as handler]))

(def routes
  #{["/api/apartments"
     :get handler/apartments-handler
     :route-name ::apartments]
    ["/api/new-apartment"
     :post [(body-params/body-params) params/keyword-params handler/new-apartment-handler]
     :route-name ::new-apartments]
    ["/api/assign-tenant"
     :post [(body-params/body-params) params/keyword-params handler/assign-tenant-handler]
     :route-name ::assign-tenant]})