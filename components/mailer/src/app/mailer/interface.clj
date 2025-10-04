(ns app.mailer.interface
  (:require [app.mailer.core :as core]))

(defn send-email [to subject attachments]
  (core/send-email to subject attachments))