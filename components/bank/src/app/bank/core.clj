(ns app.bank.core
  (:require ;;[io.pedestal.http.body-params :as body-params]
            ;;[io.pedestal.http.params :as params] 
   [hiccup2.core :as h]
   [app.html.interface :as html]
   [app.html.layout :as layout]
   [app.bank.list :as list]
   [app.bank.persistance :as persistance]))

(def bank-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Bank accounts" :content (list/content (persistance/list-accounts)) :menu-id (:main-menu-4 layout/menu-id)}]
              (assoc context :response (html/respond-with-params layout/content {:content content} (:title content)))))})

#_(defn get-body-response [params]
  ;;(str "<div><p>New feature params: " params "</p></div>")
  (let [result [:div "New feature params: " [:ul (for [[key value] params] [:li key " -> " value])]]]
    (str (h/html result))))

#_(defn post-new-feature-handler [context];;Test function
  (let [session (-> context :session)
        params (-> context :form-params)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (get-body-response params)}))

(defn get-acc-detail []
  (let [result [:div "Load content from database:" [:p "Account details"]]]
    (str (h/html result))))

(comment
  (println (get-acc-detail))
  )

(def account-detail-handler
  {:name ::get
   :enter (fn [context]
           (assoc context :response {:status 200 
                                     :headers {"Content-Type" "text/html"} 
                                     :body (get-acc-detail)}))})

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