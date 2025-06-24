(ns app.property.core
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params] 
            [app.html.interface :as html]
            [app.excel.interface :as excel]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [app.property.list :as properties]
            [app.property.persistance :as persistance]))

(defrecord PropertyComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema))

  (stop [component]))

(defn property-component [config]
  (map->PropertyComponent config))

(defn create-property [])

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


(def internal-routes
  #{["/property/:name"
     :get [(body-params/body-params) params/keyword-params propertie-handler]
     :route-name ::propertie]
    ["/upload-property-details"
     :post [(ring-mw/multipart-params) post-upload-property-handler]
     :route-name ::upload-property-details]})