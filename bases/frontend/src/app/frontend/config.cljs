(ns app.frontend.config)

(def api-url
  (if goog.DEBUG
    "http://localhost:8080/api/questions"
    "https://immo.busqandote.com/api/questions"))