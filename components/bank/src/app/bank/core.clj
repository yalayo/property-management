(ns app.bank.core
  (:require ;;[io.pedestal.http.body-params :as body-params]
            ;;[io.pedestal.http.params :as params] 
   [hiccup2.core :as h]
   [app.html.interface :as html]
   [app.html.layout :as layout]
   [app.bank.list :as list]
   [app.bank.account-details :as account]
   [app.bank.persistance :as persistance]))

(def bank-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Bank accounts" :content (list/content (persistance/list-accounts)) :menu-id (:main-menu-4 layout/menu-id)}]
              (assoc context :response (html/respond-with-params layout/content {:content content} (:title content)))))})

(defn get-acc-detail [context] 
  (let [params ((context :request) :params)
        id (get params :acc)]
  (str (h/html (account/content id)))))

(comment
  (println (get-acc-detail "3"))
  )

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
    ["/account-detail"
     :get account-detail-handler
     :route-name ::account-detail]})

(def internal-routes
  #{["/bank"
     :get bank-handler
     :route-name ::bank]})