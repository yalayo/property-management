(ns app.core.system-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [fugato.core :as fugato]
            [app.core.fugato :as f]))
  
(defspec model-eq-reality 10
  (prop/for-all [commands (fugato/commands f/model f/initial-state 10 1)]
                (= (f/run f/initial-state commands) (-> commands last meta :after))))