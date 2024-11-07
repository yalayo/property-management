(ns app.letter.interface
  (:require [app.letter.core :as core]))

(defn create [headers content]
  (core/create headers content))