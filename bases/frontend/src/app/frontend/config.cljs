(ns app.frontend.config
  (:require [clojure.string :as str]))

(defn internal? []
  (str/includes? (.-host js/window.location) "localhost:9090"))

(def api-url
  (if goog.DEBUG
    (if (internal?)
      "http://localhost:9090"
      "http://localhost:8080")
    "https://immo.busqandote.com"))