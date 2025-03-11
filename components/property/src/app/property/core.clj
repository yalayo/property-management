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
            (let [content {:title "Properties" :content (properties/content (persistance/list-properties))}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Properties"))))})

(def new-property-handler
  {:name ::post
   :enter (fn [context]
            (println "Test")
            (let [params (-> context :request :params)
                  {:keys [name]} params]
              (println "Property name: " name)
              (persistance/create-property name)
              (assoc context :response (html/respond new-property/get-new-property-form "New property"))))})

(def propertie-handler
    {:name ::get
    :enter (fn [context]
             (let [session (-> context :request :session)
                   name (-> context :request :path-params :name)]
               (println "Recarga la pagina del handler que envia el form")
               (assoc context :response (html/respond-with-params properties/get-property-details name"Property"))))})

(def post-upload-clients-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (if (some? file-input-stream)
                (let [result (flatten (excel/property-bank-data file-input-stream))]
                    (assoc context :response {:status 200
                                              :headers {"HX-Redirect" "/clients"}
                                              :session {:tenants result}})))))})

(def routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]})

(def internal-routes
  #{["/properties"
     :get properties-handler
     :route-name ::properties]
    ["/propertie/:name"
     :get [(body-params/body-params) params/keyword-params propertie-handler]
     :route-name ::propertie]
    ["/upload-property-details"
     :post [(ring-mw/multipart-params) post-upload-clients-handler]
     :route-name ::upload-clients-details]
    ["/new-property"
     :post [(body-params/body-params) params/keyword-params new-property-handler]
     :route-name ::new-property]})