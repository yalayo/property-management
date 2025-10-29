(ns app.operations.handler
  (:require [app.operations.core :as core]
            [app.operations.persistance :as persistance]
            [app.letter.interface :as letter]
            [app.excel.interface :as excel]
            [app.mailer.interface :as mailer])
  (:import (java.util Base64)))

(def operations-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response {:status 200 :body (persistance/list-operations) :headers {"Content-Type" "text/edn"}}))})

(def new-operation-handler
  {:name ::post
   :enter (fn [context]
            (core/store-operation (-> context :request :edn-params))
            (assoc context :response {:status 200}))})

(def post-upload-details-handler
  {:name ::post
   :enter (fn [context]
            (let [multipart-data (:multipart-params (-> context :request))
                  file (get multipart-data "file")
                  file-input-stream (:tempfile file)]
              (when (some? file-input-stream)
                (let [result (flatten (excel/process file-input-stream))]
                  (if (some #(:error %) result)
                    (assoc context :response {:status 500 :body (filter :error result) :headers {"Content-Type" "text/edn"}})
                    (do
                      (persistance/store-property-info result)
                      (assoc context :response {:status 200 :body result :headers {"Content-Type" "text/edn"}})))))))})

(def post-create-letter-handler
  {:name ::get
   :enter (fn [context]
            (let [info (-> context :request :edn-params)
                  letter (letter/create info)]
              (assoc context :response {:status 200
                                        :headers {"Content-Type" "application/pdf" "Content-Disposition" "attachment; filename=letter.pdf"}
                                        :body (java.io.ByteArrayInputStream. letter)})))})

(defn base64-encode [bytes]
  (.encodeToString (Base64/getEncoder) bytes))

(defn create-attachments [letters]
  (map (fn [letter]
         (let [pdf-base64 (base64-encode letter)]
           {:filename "todo.pdf" :content pdf-base64 :type "application/pdf"})) letters))

(def post-send-letters-handler
  {:name ::get
   :enter (fn [context]
            (let [info (-> context :request :edn-params)
                  letters (letter/create-all info)
                  attachments (create-attachments letters)]
              (mailer/send-email "yuninho2005@gmail.com" (str "Letter for  property: " "Property name here") attachments)
              (assoc context :response {:status 200 :body "Letters send"})))})