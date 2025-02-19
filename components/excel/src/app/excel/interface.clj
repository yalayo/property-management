(ns app.excel.interface
  (:require [app.excel.core :as core]))

(defn list-tenants [input-stream]
  (core/list-tenants input-stream))

(defn process [input-stream]
  (core/process input-stream))

(defn extract-client-data [input-stream]
  (core/extract input-stream))