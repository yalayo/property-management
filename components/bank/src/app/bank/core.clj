(ns app.bank.core
  (:require [io.pedestal.http.ring-middlewares :as ring-mw]
            ;;[io.pedestal.http.body-params :as body-params]
            ;;[io.pedestal.http.params :as params]
            [app.html.core :refer [auth-required]]
            [hiccup2.core :as h]
            [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.bank.list :as list]
            [app.bank.list-transactions :as transactions]
            [app.bank.account-details :as account]
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
                  file-input-stream (:tempfile file)
                  result (statement/process file-input-stream)]
              (assoc context :response (html/respond-with-params transactions/content result "Bank accounts"))))})

(defn get-acc-detail [context] 
  (let [params ((context :request) :params)
        id (get params :acc)]
  (str (h/html (account/content id)))))

(def account-detail-handler
  {:name ::get
   :enter (fn [context] 
            (assoc context :response {:status 200 
                                     :headers {"Content-Type" "text/html"} 
                                     :body (get-acc-detail context)}))})

(def routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]
    ["/upload-transactions"
     :post [(ring-mw/multipart-params) auth-required post-upload-transactions-handler]
     :route-name ::post-upload-transactions]
    ["/account-detail"
     :get account-detail-handler
     :route-name ::account-detail]})

(def internal-routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})