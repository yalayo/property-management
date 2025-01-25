(ns app.flags.core
  (:require [app.html.interface :as html]
            [app.flags.list :as flags]))

(def flags-handler
  {:name ::get
   :enter (fn [context]
            (assoc context :response (html/respond-with-params flags/content [] "Feature flags")))})

(def routes
  #{["/flags"
     :get flags-handler
     :route-name ::flags]})