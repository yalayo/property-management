(ns app.user.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.user.handler :as handler]))

(def routes
  #{["/api/sign-in" :post [(body-params/body-params) params/keyword-params handler/post-sign-in]
     :route-name ::post-sign-in]
    ["/api/sign-up" :post [(body-params/body-params) params/keyword-params handler/post-sign-up]
     :route-name ::post-sign-up]
    ["/api/change-password"
     :post [(body-params/body-params) params/keyword-params handler/post-change-password]
     :route-name ::post-change-password]})