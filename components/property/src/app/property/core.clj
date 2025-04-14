(ns app.property.core
  (:require [io.pedestal.http.body-params :as body-params]
            [hiccup2.core :as h]
            [io.pedestal.http.params :as params]
            [app.html.interface :as html]
            [app.excel.interface :as excel]
            [app.html.upload-details :as upload-details]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [app.html.layout :as layout]
            [app.property.list :as properties]
            [app.property.persistance :as persistance]
            [app.property.new-property :as new-property]))

(def properties-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Properties" :content (properties/content (persistance/list-properties)) :menu-id (:main-menu-3 layout/menu-id)}]
              (assoc context :response (html/respond-with-params layout/content {:content content} (content :title)))))})

(def new-property-handler
  {:name ::post
   :enter (fn [context]
            (persistance/create-property (-> context :request :edn-params))
            (assoc context :response {:status 200}))})

(def propertie-handler
    {:name ::get
    :enter (fn [context]
             (let [session (-> context :request :session)
                   name (-> context :request :path-params :name)]
               (println "Show the form to upload property")
               (assoc context :response (html/respond-with-params properties/get-property-details name "Property"))))})

(def post-upload-property-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (println "Enter to the handler to proccess the propertie's bank data")
              (if (some? file-input-stream)
                (let [result (flatten (excel/property-bank-data file-input-stream))]
                    (assoc context :response {:status 200
                                              :headers {"HX-Redirect" "/clients"}
                                              :session {:tenants result}})))))})

(def routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]
    ["/new-property"
     :post [(body-params/body-params) params/keyword-params new-property-handler]
     :route-name ::new-property]})

(def internal-routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]
    ["/property/:name"
     :get [(body-params/body-params) params/keyword-params propertie-handler]
     :route-name ::propertie]
    ["/upload-property-details"
     :post [(ring-mw/multipart-params) post-upload-property-handler]
     :route-name ::upload-property-details]
    ["/new-property"
     :post [(body-params/body-params) params/keyword-params new-property-handler]
     :route-name ::new-property]})