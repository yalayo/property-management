(ns app.excel.handler
  (:require [app.excel.core :as core]
            [app.worker.cf :as cf]
            [app.worker.async :refer [js-await]]))

(defn post-upload-details [_ request _ _]
  (js-await [form-data (.formData request)
             file      (.get form-data "file")
             email     (.get form-data "email")
             buf       (.arrayBuffer file)
             result    (core/process buf)]
            (println "Uploading ... file name:" (.-name file) ", email:" email)
            (cf/response-edn {:result result
                              :email  email
                              :file   (.-name file)}
                             {:status 200})))

#_(defn post-upload-details [route request env ctx]
    (js-await [form-data (.formData request)
               file      (.get form-data "file")
               email     (.get form-data "email")
               buf       (.arrayBuffer file)
               result    (core/process buf)]
              (println "Uploading ... file name:" (.-name file) ", email:" email)
              (cf/response-edn {:result result
                                :email  email
                                :file   (.-name file)}
                               {:status 200})))