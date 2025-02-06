(ns app.flags.core
  (:require [app.html.interface :as html]
            [app.html.dashboard :as layout]
            [app.flags.list :as flags]))

(def flags-handler
  {:name ::get
   :enter (fn [context]
            (let [content {:title "Feature flags" :content (flags/content [])}]
              (assoc context :response (html/respond-with-params layout/content {:content content} "Feature flags"))))})

(def routes
  #{["/flags"
     :get flags-handler
     :route-name ::flags]})