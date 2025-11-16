(ns app.excel.routes
  (:require [app.excel.handler :as handler]))

(def routes [["/upload-details" {:post handler/post-upload-details}]])