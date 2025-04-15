(ns app.survey.routes
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.survey.persistance :as persistance]))

(def get-questions-handler
  {:name ::get
   :enter (fn [context]
            (let [content (persistance/list-questions)]
              (assoc context :response {:status 200
                                        :body content
                                        :headers {"Content-Type" "text/edn" "Access-Control-Allow-Origin" "*.busqandote.com"}})))})

(def post-survey-handler
  {:name ::post
   :enter (fn [context]
            (persistance/store-survey-responses (-> context :request :edn-params))
            (assoc context :response {:status 200
                                      :headers {"Content-Type" "text/edn" "Access-Control-Allow-Origin" "*.busqandote.com"}}))})

(def external
  #{["/api/questions"
     :get get-questions-handler
     :route-name ::get-questions]
    ["/api/survey"
     :post [(body-params/body-params) params/keyword-params post-survey-handler]
     :route-name ::post-survey]})