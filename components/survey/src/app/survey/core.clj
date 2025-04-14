(ns app.survey.core
  (:require [com.stuartsierra.component :as component]
            [app.survey.persistance :as persistance]))

(defrecord SurveyComponent [config]
  component/Lifecycle

  (start [component]
    (persistance/transact-schema)
    
    (when (empty? (persistance/list-questions))
      (println "Creating survey questions ...")
      (persistance/create-questions)))

  (stop [component]))

(defn survey-component [config]
  (map->SurveyComponent config))