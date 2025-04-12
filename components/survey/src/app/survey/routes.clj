(ns app.survey.routes)

(def get-questions-handler
  {:name ::get
   :enter (fn [context]
            (let [content [{:id 1 :text "Do you own multiple rental properties?"}
                           {:id 2 :text "Do you struggle with tracking tenant payments?"}
                           {:id 3 :text "Do you currently use Excel to manage your properties?"}]]
              (assoc context :response {:status 200
                                        :body content
                                        :headers {"Content-Type" "text/edn" "Access-Control-Allow-Origin" "*"}})))})

(def external
  #{["/api/questions"
     :get get-questions-handler
     :route-name ::get-questions]})