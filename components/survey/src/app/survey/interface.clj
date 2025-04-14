(ns app.survey.interface
  (:require [app.survey.core :as core]
            [app.survey.routes :as routes]))

(defn survey-component [config]
  (core/survey-component config))

(defn get-routes []
  routes/external)