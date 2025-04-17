(ns app.frontend.config)

(def api-url
  (if goog.DEBUG
    "http://localhost:8080"
    "https://immo.busqandote.com"))