(ns app.excel.handler
  (:require [app.excel.core :as core]
            [app.worker.cf :as cf]
            [app.worker.async :refer [js-await]]))

(defn post-upload-details [route request env ctx]
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

#_(defn iterator->seq [it]
  (when-some [n (.next it)]
    (when-not (.-done n)
      (cons (.-value n)
            (lazy-seq (iterator->seq it))))))




#_(defn post-upload-details [route request env ctx]
  ;; Print the request metadata (URL, headers, etc.)
  (println "Request:" (.-url request))
  (println "Headers:")
  (doseq [[k v] (js->clj (.-headers request))]
    (println " " k ":" v))

  ;; Now inspect multipart fields
  (js-await [form-data (.formData request)]
            (println "Form parts:")
  
            (doseq [[k v] (iterator->seq (.entries form-data))]
              (cond
                ;; File-like?
                (and (object? v) (.-arrayBuffer v))
                (println " " k "-> FILE" {:name (.-name v)
                                          :size (.-size v)
                                          :type (.-type v)})
  
                :else
                (println " " k "->" v)))
  
            (cf/response-edn {:ok true} {:status 200})))