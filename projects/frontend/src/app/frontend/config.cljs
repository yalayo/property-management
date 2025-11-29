(ns app.frontend.config
  (:require [clojure.string :as str]))

(defn cloudflare-dev? []
  (str/includes? (.-host js/window.location) "localhost:8081"))

(defn cloudflare-prod? []
  (str/includes? (.-host js/window.location) "anudis.com"))

(defn internal? []
  (str/includes? (.-host js/window.location) "localhost:9090"))

(def api-url
  (if goog.DEBUG
    (if (cloudflare-dev?)
      "http://localhost:8787"
      (if (internal?)
        "http://localhost:9090"
        "http://localhost:8080"))
    (if (cloudflare-prod?)
      "https://backend.anudis.com"
      "https://immo.busqandote.com")))