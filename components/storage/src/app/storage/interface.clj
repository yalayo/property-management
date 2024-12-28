(ns app.storage.interface
  (:require [app.storage.core :as core]))

(defn datasource-component [config]
  (core/datasource-component config))