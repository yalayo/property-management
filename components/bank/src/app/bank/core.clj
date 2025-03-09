(ns app.bank.core
  (:require ;;[io.pedestal.http.body-params :as body-params]
            ;;[io.pedestal.http.params :as params]
            [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.bank.list :as list]
            [app.bank.persistance :as persistance]))

(def bank-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Bank" :content (list/content (persistance/list-accounts))}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Bank"))))})


(def routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})

(def internal-routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})