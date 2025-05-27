(ns app.letter.interface
  (:require [app.letter.core :as core]))

(defn create [info]
  (core/create info))