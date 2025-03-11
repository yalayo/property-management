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
            (let [content {:title "Bank accounts" :content (list/content (persistance/list-accounts)) :menu-id (:main-menu-4 layout/menu-id)}]
              (assoc context :response (html/respond-with-params layout/content {:content content} (:title content)))))})


(def routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})

(def internal-routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})