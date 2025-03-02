(ns app.flags.core
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.params :as params]
            [app.html.interface :as html]
            [app.html.layout :as layout]
            [app.flags.list :as flags]
            [app.flags.new-flag :as new-flag]
            [app.property.persistance :as persistance]))

(def flags-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Feature flags" :content (flags/content [])}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Feature flags"))))})

(def new-flag-handler
  {:name ::post
   :enter (fn [context]
            (println "Params: " (-> context :request :params))
            (let [params (-> context :request :params)
                  flag-name (:name params)]
              (assoc context :response (html/respond new-flag/get-new-flag-form "New flag"))))})

(def routes
  #{["/flags"
     :get flags-handler
     :route-name ::flags]
    ["/new-flag"
     :post [(body-params/body-params) params/keyword-params new-flag-handler]
     :route-name ::new-flag]})