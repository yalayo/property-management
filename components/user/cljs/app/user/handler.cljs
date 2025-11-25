(ns app.user.handler
  (:require
   [app.user.persistance :as persistance]
   [app.worker.async :refer [js-await]]
   [app.worker.cf :as cf]))

(defn post-sign-in [_ request _ _]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

(defn post-sign-up [_ request _ _]
  (let [data (cf/request->edn request)
        email (:email data)
        password (:password data)]
    (println "Data: " data)
    (persistance/create-account email password)))

#_(defmethod handle-route [:post-upload-details :POST]
  [route request env ctx]
  (js-await [form-data (.formData request)
             file (.get form-data "file")
             buf (.arrayBuffer file)
             result {} #_(excel/process buf)]
            (cf/response-edn {:result result} {:status 200})))
