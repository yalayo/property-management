(ns app.user.handler
  (:require
   [lib.async :refer [js-await]]
   [server.cf :as cf]))

#_(defmethod handle-route [:post-sign-up :POST] [route request env ctx]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

#_(defmethod handle-route [:post-upload-details :POST]
  [route request env ctx]
  (js-await [form-data (.formData request)
             file (.get form-data "file")
             buf (.arrayBuffer file)
             result {} #_(excel/process buf)]
            (cf/response-edn {:result result} {:status 200})))
