(ns app.account.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.account.handler :as handler]))

(def routes
  #{["/api/accounts"
     :get handler/accounts-handler
     :route-name ::accounts]
    ["/api/new-account"
     :post [(body-params/body-params) params/keyword-params handler/new-account-handler]
     :route-name ::new-accounts]})