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
           [app.letter.interface :as letter]
           [cheshire.core :as json]
           [clj-http.client :as client]
           [app.html.user-buildings :as user-buildings]
           [app.html.building-apartments :as building-apartments]
           [app.html.apartment-details :as apartment-datails])
  (:import [java.util UUID]))

;; Prepare the hicup to return it as html
(defn template [html-body title]
  [:html
   [:head
    [:title title] 
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
    [:link {:href "tailwind.min.css" :rel "stylesheet"}]
    [:script {:src "htmx.min.js"}]]
   [:body (h/raw html-body)]])

(defn ok [body]
  {:status 200
   :headers {"Content-Type" "text/html" "Content-Security-Policy" "img-src 'self'"}
   :body (-> body
             (h/html)
             (str))})

(defn respond [content title]
  (ok (template (str (h/html (content))) title)))

(defn respond-with-params [content value title]
  (ok (template (str (h/html (content value))) title)))

(def auth-required
  (interceptor
   {:name ::auth-required
    :enter (fn [context ]
             (let [session (-> context :request :session)]
               (if (empty? session)
                 (assoc context :response {:status 302 :headers {"Location" "/sign-in"}})
                 context)))}))

(def index-page-handler
  {:name ::index
   :enter (fn [context]
            (assoc context :response {:status 302 :headers {"Location" "/sign-in"}}))})

(defn dashboard-handler [context]
  (let [session (-> context :requet :session)
        dashboard-content {:title "Dashboard" :content "Insert here your page content!" :menu-id (:dashboard dashboard/menu-id)}]
    (if (empty? session)
      (response/redirect "/sign-in")
      (respond-with-params dashboard/content {:email (:email session) :created-at (:created-at session) :content dashboard-content} "Dashboard"))))

(def upload-details-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response (respond upload-details/page "Hochladen")))})

(def upload-details-handler-2
  {:name ::get
   :enter (fn [context]
            (assoc context :response (respond upload-details/page2 "Hochladen")))})

(def post-upload-details-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (if (some? file-input-stream) 
                (let [result (flatten (excel/process file-input-stream))]
                  (if (some #(:error %) result)
                    (assoc context :response (respond-with-params upload-details/wrong-file-selected result "Hochladen"))
                    (assoc context :response {:status 200
                                              :headers {"HX-Redirect" "/tenants"}
                                              :session {:tenants result}})))
                (assoc context :response (respond upload-details/no-file-selected "Hochladen")))))})

(def post-upload-details-handler-2
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (if (some? file-input-stream)
                (let [result (flatten (excel/process file-input-stream))]
                  (if (some #(:error %) result)
                    (assoc context :response (respond-with-params upload-details/wrong-file-selected2 result "Hochladen"))
                    (assoc context :response {:status 200
                                              :headers {"HX-Redirect" "/tenants"}
                                              :session {:tenants result}})))
                (assoc context :response (respond upload-details/no-file-selected2 "Hochladen")))))})

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
              (assoc context :response (respond-with-params tenants/content (:tenants session) "Mieter(innen) Liste"))))})

(def create-letter-handler
  {:name ::get
   :enter (fn [context]
            (let [tenants (-> context :request :session :tenants)
                  tenant-id (-> context :request :path-params :tenant-id)
                  tenant (some #(when (= (:tenant-id %) tenant-id) %) tenants)]
              (assoc context :response {:status 200
                                        :headers {"Content-Type" "application/pdf" "Content-Disposition" "inline; filename=letter.pdf"}
                                        :body (java.io.ByteArrayInputStream. (letter/create tenant))})))})

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")

(defn email-verification [email]
  (cond
    (empty? email) "Email cannot be empty."
    (not (re-matches email-regex email)) "Email format is not valid."
    :else nil))

(def upload-details-email
   {:name ::get
    :enter (fn [context]
             (assoc context :response (respond upload-details/email-matters-aux "Umfrage")))})

(def sendgrid-api-key "YOUR_SENDGRID_API_KEY");;Env: SENDGRID_API_KEY

(defn generate-verification-link [email]
  (let [uuid (str (UUID/randomUUID))]
    (str "http://localhost:8080/verify-email?email=" email "&token=" uuid)))

(defn send-verification-email [email]
  (let [verification-link (generate-verification-link email)
        sendgrid-url "https://api.sendgrid.com/v3/mail/send"
        payload {:personalizations [{:to [{:email email}]}]
                 :from {:email "no-reply@yourdomain.com"}
                 :subject "Email Verification"
                 :content [{:type "text/plain"
                            :value (str "Click the following link to verify your email: " verification-link)}]}]
    (client/post sendgrid-url
                 {:headers {"Authorization" (str "Bearer " sendgrid-api-key)
                            "Content-Type" "application/json"}
                  :body (json/generate-string payload)})))

(def post-email-handler
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :params)
                  email (:email params)
                  error-message (email-verification email)]
              (if (not (empty? error-message))
                (assoc context :response (respond-with-params upload-details/email-matters error-message "Umfrage"))
                (do
                  (println "Received email:" email)
                  (send-verification-email email)
                  (assoc context :response {:status 200
                                            :headers {"HX-Redirect" "/thank-you"}})))))})

(defn mark-user-as-verified [email token]
  (if (and email token)
    {:status :success, :message "Email successfully verified."}
    {:status :error, :message "Invalid verification link."}))

;;Test: http://localhost:8080/verify-email?email=pruebas@seudominio.com&token=12345678-1234-5678-1234-567812345678
(def verify-email-handler
  {:name ::get
   :enter (fn [context]
            (let [params (-> context :request :query-params)
                  email (:email params)
                  token (:token params)
                  verification-result (mark-user-as-verified email token)]
              (println verification-result)
              (if (= (:status verification-result) :success)
                (assoc context :response (respond upload-details/email-succes-checked "Email Prüfung"))
                (assoc context :response (respond upload-details/email-error-checking "Email Prüfung")))))})

#_(defn user-buildings-handler [context] 
  (let [session (-> context :request :session) 
        content {:title "Buildings" :content (user-buildings/get-buildings) :menu-id (:buildings dashboard/menu-id)}]
      (if (empty? session)
        (response/redirect "/sign-in")
        (respond-with-params dashboard/content {:email (:email session) :created-at (:created-at session) :content content} (:title content)))))

#_(defn building-apartments-post-handler [context]
    (let [session (-> context :request :session)
          params (-> context :request :form-params)
          content {:title "Building apartments" :content (building-apartments/get-building-apartments params) :menu-id (:buildings dashboard/menu-id)}]
      (if (empty? session)
        (response/redirect "/sign-in")
        (respond-with-params dashboard/content {:email (:email session) :created-at (:created-at session) :content content} (:title content)))))

(defn user-buildings-handler [context];;Test function to ignore session data (http://localhost:8080/user-buildings)
  (let [session (-> context :session)
        content {:title "Buildings" :content (user-buildings/get-buildings) :menu-id (:buildings dashboard/menu-id)}
        dashboard-content {:email "prop#example.com" :created-at "2025-01-29" :content content}]
    (if (empty? session)
      (respond-with-params dashboard/content dashboard-content (:title content))
      (response/redirect "/sign-in"))))

(defn building-apartments-post-handler [context];;Test function to ignore session data (it's called inside /user-buildings)
  (let [session (-> context :session)
        params (-> context :form-params)
        content {:title "Building apartments" :content (building-apartments/get-building-apartments params) :menu-id (:buildings dashboard/menu-id)}
        dashboard-content {:email "prop#example.com" :created-at "2025-01-29" :content content}]
    (if (empty? session)
      (respond-with-params dashboard/content dashboard-content (:title content))
      (response/redirect "/sign-in"))))

(defn post-apartment-datails-handler [context];;Test function to ignore session data (it's called inside /building-apartments)
  (let [session (-> context :session)
        params (-> context :form-params)
        content {:title "Apartment details" :content (apartment-datails/get-apartment-details params) :menu-id (:buildings dashboard/menu-id)}
        dashboard-content {:email "prop#example.com" :created-at "2025-01-29" :content content}]
    (if (empty? session)
      (respond-with-params dashboard/content dashboard-content (:title content))
      (response/redirect "/sign-in"))))

(def routes
  #{["/"
     :get [index-page-handler]
     :route-name ::index]
    ["/upload-excel"
     :get [(body-params/body-params) auth-required upload-details-handler]
     :route-name ::upload-excel]
    ["/upload-details"
     :post [(ring-mw/multipart-params) auth-required post-upload-details-handler]
     :route-name ::post-upload-details]
    ["/upload-excel-2"
     :get [(body-params/body-params) upload-details-handler-2]
     :route-name ::upload-excel-2]
    ["/upload-details-2"
     :post [(ring-mw/multipart-params) post-upload-details-handler-2]
     :route-name ::post-upload-details-2]
    ["/dashboard"
     :get [(body-params/body-params) auth-required dashboard-handler]
     :route-name ::dashboard] 
    ["/questions"
     :get [(body-params/body-params) upload-details-email]
     :route-name ::questions]
    ["/questions"
     :post [(body-params/body-params) params/keyword-params post-email-handler]
     :route-name ::post-questions]
    ["/verify-email"
     :get [params/keyword-params verify-email-handler]
     :route-name ::verify-email] 
    ["/letter"
     :get [letter-handler]
     :route-name ::letter]
    ["/tenants"
     :get [tenants-handler]
     :route-name ::tenants]
    ["/tenants/:tenant-id"
     :get [params/keyword-params create-letter-handler]
     :route-name ::create-letter]
     ["/user-buildings"
      :get user-buildings-handler;TODO include auth-required
      :route-name ::user-buildings] 
    ["/building-apartments"
     :post [(body-params/body-params) building-apartments-post-handler];TODO include auth-required
     :route-name ::building-apartments-post]
    ["/apartment-datails"
     :post [(body-params/body-params) post-apartment-datails-handler];TODO include auth-required
     :route-name ::post-apartment-datails]})
