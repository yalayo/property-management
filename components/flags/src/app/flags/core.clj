(ns app.flags.core
  (:require [app.html.interface :as html]
            [app.html.dashboard :as layout]
            [app.flags.list :as flags]))

(def flags-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Feature flags" :content (flags/content [])}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Feature flags"))))})

(def new-flag-handler
  {:name ::post
   :enter (fn [context]
            (let [params (-> context :request :params)
                  flag-name (:name params)]
              (assoc context :response (flags/flag-info {:name flag-name}))))})

(def routes
  #{["/flags"
     :get flags-handler
     :route-name ::flags]
    ["/new-flag"
     :post new-flag-handler
     :route-name ::new-flag]})