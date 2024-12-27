(ns app.excel.interface
  (:require [app.excel.core :as core]))

(defn process [input-stream]
  (core/process input-stream))