(ns app.user.routes
  (:require [app.user.handler :as handler]))

(def routes [["/sign-in" {:name :sign-in :post handler/post-sign-in}]
             ["/sign-up" {:name :sign-un :post handler/post-sign-up}]])