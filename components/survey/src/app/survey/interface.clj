(ns app.survey.interface
  (:require [app.survey.routes :as routes]))

(defn get-routes []
  routes/external)