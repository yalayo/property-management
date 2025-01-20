(ns app.logs.interface
  (:require [app.logs.core :as core]))

(defn telegram-publisher [config]
  (core/telegram-publisher config))