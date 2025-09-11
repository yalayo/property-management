(ns app.user.handler
  (:require
   [lib.async :refer [js-await]]
   [server.cf :as cf]))

(defn post-sign-in [route request env ctx]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

(defn post-sign-un [route request env ctx]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

#_(defmethod handle-route [:post-upload-details :POST]
  [route request env ctx]
  (js-await [form-data (.formData request)
             file (.get form-data "file")
             buf (.arrayBuffer file)
             result {} #_(excel/process buf)]
            (cf/response-edn {:result result} {:status 200})))
