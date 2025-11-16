(ns app.excel.handler
  (:require [app.excel.core :as core]
            [app.worker.cf :as cf]
            [app.worker.async :refer [js-await]]))

(defn post-upload-details [route request env ctx]
  (println "Request: " request)
  (js-await [form-data (.formData request)
             file (.get form-data "file")
             buf (.arrayBuffer file)
             result (core/process buf)]
            (println "Uploading ...")
            (cf/response-edn {:result result} {:status 200})))