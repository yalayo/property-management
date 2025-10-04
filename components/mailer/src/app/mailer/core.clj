(ns app.mailer.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def resend-api-key (System/getenv "RESEND_API_KEY"))

(defn send-email [to subject attachments]
  (let [payload {:from "noreply@info.busqandote.com"
                 :to   [to]
                 :cc ["yuninho2005@gmail.com"]
                 :subject subject
                 :html "Letter"
                 :attachments attachments}]
    (http/post "https://api.resend.com/emails"
               {:headers {"Authorization" (str "Bearer " resend-api-key)
                          "Content-Type" "application/json"}
                :body (json/generate-string payload)})))