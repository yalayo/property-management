(ns app.operations.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [app.operations.handler :as handler]))

(def routes
  #{["/api/operations"
     :get handler/operations-handler
     :route-name ::operations]
    ["/api/new-operation"
     :post [(body-params/body-params) params/keyword-params handler/new-operation-handler]
     :route-name ::new-operation]})

(defn get-routes []
  #{["/api/upload-details"
     :post [(ring-mw/multipart-params) handler/post-upload-details-handler]
     :route-name ::post-upload-details]
    ["/api/create-letter"
     :post [(body-params/body-params) params/keyword-params handler/post-create-letter-handler]
     :route-name ::post-create-letter-handler]
    ["/api/send-letters"
     :post [(body-params/body-params) params/keyword-params handler/post-send-letters-handler]
     :route-name ::post-send-letters-handler]})