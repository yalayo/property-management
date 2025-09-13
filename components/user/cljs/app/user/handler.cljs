(ns app.user.handler
  (:require
   [app.user.persistance :as persistance]
   [lib.async :refer [js-await]]
   [server.cf :as cf]))

(defn post-sign-in [route request env ctx]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

(defn post-sign-up [route request env ctx]
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
