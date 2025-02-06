(ns app.excel.interface
  (:require [app.excel.core :as core]))

(defn process [input-stream]
  (core/process input-stream))

(defn extract-client-data [input-stream]
  (core/extract input-stream))