(ns app.user.handler
  (:require
   [app.user.persistance :as persistance]
   [app.worker.async :refer [js-await]]
   [app.worker.cf :as cf]))

(defn post-sign-in [_ request _ _]
  (js-await [data (cf/request->edn request)]
            (println "Register: " data)))

(defn post-sign-up [_ request _ _]
  (js-await [data (cf/request->edn request)]
            (let [{:keys [user name password]} data]
              (persistance/create-account user password))))
