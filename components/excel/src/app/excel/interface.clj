(ns app.excel.interface
  (:require [app.excel.core :as core]))

(defn process-details [data]
  (core/process-details data))

(defn process [input-stream]
  (core/process input-stream))