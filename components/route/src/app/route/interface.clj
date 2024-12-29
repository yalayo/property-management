(ns app.route.interface
  (:require [app.route.core :as core]))

(defn route-component [config]
  (core/route-component config))