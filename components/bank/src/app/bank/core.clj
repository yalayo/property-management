(ns app.bank.core
  (:require [io.pedestal.http.ring-middlewares :as ring-mw]
            ;;[io.pedestal.http.body-params :as body-params]
            ;;[io.pedestal.http.params :as params]
            [app.html.core :refer [auth-required]]
            [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.bank.list :as list]
            [app.bank.persistance :as persistance]
            [app.bank.statement :as statement]))

(def bank-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Bank accounts" :content (list/content (persistance/list-accounts)) :menu-id (:main-menu-4 layout/menu-id)}]
              (assoc context :response (html/respond-with-params layout/content {:content content} (:title content)))))})

(def post-upload-transactions-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (statement/process file-input-stream)
              #_(if (some? file-input-stream)
                (let [result (flatten (excel/process file-input-stream))]
                  (if (some #(:error %) result)
                    (assoc context :response (html/respond-with-params upload-details/wrong-file-selected result "Hochladen"))
                    (assoc context :response {:status 200
                                              :headers {"HX-Redirect" "/tenants"}
                                              :session {:tenants result}})))
                (assoc context :response (respond upload-details/no-file-selected "Hochladen")))))})

(def routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]
    ["/upload-transactions"
     :post [(ring-mw/multipart-params) auth-required post-upload-transactions-handler]
     :route-name ::post-upload-transactions]})

(def internal-routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})