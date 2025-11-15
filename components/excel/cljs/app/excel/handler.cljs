(ns app.excel.handler
  (:require [app.excel.core :as core]
            [server.cf :as cf]
            [lib.async :refer [js-await]]
            [server.core :refer [handle-route]]))

(defmethod handle-route [:post-upload-details :POST]
  [route request env ctx]
  (js-await [form-data (.formData request)
             file (.get form-data "file")
             buf (.arrayBuffer file)
             result (core/process buf)]
            (println "Uploading ...")
            (cf/response-edn {:result result} {:status 200})))