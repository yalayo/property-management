(ns app.tenant.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.tenant.handler :as handler]))

(def routes
  #{["/api/tenants"
     :get handler/tenants-handler
     :route-name ::tenants]
    ["/api/new-tenant"
     :post [(body-params/body-params) params/keyword-params handler/new-tenant-handler]
     :route-name ::new-tenants]})

(defn get-routes [storage]
  #{["/api/tenants"
     :get (handler/tenants-handler storage)
     :route-name ::tenants]
    ["/api/new-tenant"
     :post [(body-params/body-params) params/keyword-params handler/new-tenant-handler]
     :route-name ::new-tenants]})