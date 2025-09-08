(ns app.letter.interface
  (:require [app.letter.core :as core]
            [app.letter.routes :as routes]))

(defn create [info]
  (core/create info))

(defn routes []
  routes/routes)