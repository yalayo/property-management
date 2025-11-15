(ns app.excel.interface
  (:require [integrant.core :as ig]
            [app.excel.core :as core]
            [app.excel.routes :as routes]))

(defn routes []
  routes/routes)

#_(defn list-tenants [input-stream]
  (core/list-tenants input-stream))

(defn process [input-stream]
  (core/process input-stream))

#_(defn extract-client-data [input-stream]
  (core/extract input-stream))

#_(defn property-bank-data [input-stream]
  (core/extract input-stream))

(defmethod ig/init-key ::routes [_ _]
  routes/routes)