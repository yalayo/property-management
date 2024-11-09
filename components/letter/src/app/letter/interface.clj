(ns app.letter.interface
  (:require [app.letter.core :as core]))

(defn create [tenant]
  (core/create tenant))