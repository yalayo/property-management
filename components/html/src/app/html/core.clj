(ns app.html.core
	(:require [hiccup2.core :as h]
            [io.pedestal.http.params :as params]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [ring.util.response :as response]
            [app.html.index :as index]
            [app.html.dashboard :as dashboard]
            [app.html.upload-details :as upload-details]
            [app.html.tenants-list :as tenants]
            [app.excel.interface :as excel]
            [io.pedestal.interceptor :refer [interceptor]]
            [app.letter.interface :as letter]))

;; Prepare the hicup to return it as html
(defn template [html-body]
  [:html
   [:head
    [:title "Title"]
    [:link {:href "tailwind.min.css" :rel "stylesheet"}]
    [:script {:src "htmx.min.js"}]]
   [:body (h/raw html-body)]])

(defn ok [body]
  {:status 200
   :headers {"Content-Type" "text/html" "Content-Security-Policy" "img-src 'self'"}
   :body (-> body
             (h/html)
             (str))})

(defn respond [content]
  (ok (template (str (h/html (content))))))

(defn respond-with-params [content value]
  (ok (template (str (h/html (content value))))))

(def auth-required
  (interceptor
   {:name ::auth-required
    :enter (fn [context ]
             (let [session (-> context :session)]
               (if (empty? session)
                 (assoc context :response {:status 302 :headers {"Location" "/sign-in"}})
                 context)))}))

(defn index-page-handler [context]
  (respond index/index-page))

(defn dashboard-handler [context]
  (let [session (-> context :session)]
    (if (empty? session)
      (response/redirect "/sign-in")
      (respond-with-params dashboard/content {:email (:email session) :created-at (:created-at session)}))))

(def upload-details-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response (respond upload-details/page)))})

(def post-upload-details-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (if (some? file-input-stream) 
                (assoc context :response {:status 200
                                          :headers {"HX-Redirect" "/tenants"}
                                          :session {:tenants (excel/process file-input-stream)}})
                (assoc context :response (respond upload-details/no-file-selected)))))})

(def letter-handler
  {:name ::get
   :enter (fn [context]
            (let [session (-> context :request :session)
                  headers (:headers session)
                  content (:content  session)]
              (assoc context :response {:status 200
                                        :headers {"Content-Type" "application/pdf" "Content-Disposition" "attachment; filename=letter.pdf"}
                                        :body (java.io.ByteArrayInputStream. (letter/create headers content))})))})

(def tenants-handler
  {:name ::get
   :enter (fn [context]
            (let [session (-> context :request :session)]
              (assoc context :response (respond-with-params tenants/content (:tenants session)))))})

(def create-letter-handler
  {:name ::get
   :enter (fn [context]
            (let [tenants (-> context :request :session :tenants)
                  tenant-id (-> context :request :path-params :tenant-id)
                  tenant (some #(when (= (:tenant-id %) tenant-id) %) tenants)]
              (assoc context :response {:status 200
                                        :headers {"Content-Type" "application/pdf" "Content-Disposition" "inline; filename=letter.pdf"}
                                        :body (java.io.ByteArrayInputStream. (letter/create tenant))})))})

(def routes
  #{["/"
     :get [(body-params/body-params) upload-details-handler]
     :route-name ::upload-details]
    ["/upload-details"
     :post [(ring-mw/multipart-params) post-upload-details-handler]
     :route-name ::post-upload-details]
    ["/dashboard"
     :get [(body-params/body-params) auth-required dashboard-handler]
     :route-name ::dashboard]
    ["/letter"
     :get [letter-handler]
     :route-name ::letter]
    ["/tenants"
     :get [tenants-handler]
     :route-name ::tenants]
    ["/tenants/:tenant-id"
     :get [params/keyword-params create-letter-handler]
     :route-name ::create-letter]})