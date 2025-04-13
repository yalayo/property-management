(ns app.survey.routes
  (:require [app.survey.persistance :as persistance]))

(def get-questions-handler
  {:name ::get
   :enter (fn [context]
            (let [content (persistance/list-questions)]
              (assoc context :response {:status 200
                                        :body content
                                        :headers {"Content-Type" "text/edn" "Access-Control-Allow-Origin" "*"}})))})

(def external
  #{["/api/questions"
     :get get-questions-handler
     :route-name ::get-questions]})