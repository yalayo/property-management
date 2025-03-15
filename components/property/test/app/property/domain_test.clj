(ns app.property.domain-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check :as tc]
            [clojure.uuid :as uuid]))

;; Generator for our entity
(def entity-gen
  (gen/hash-map
    :id (gen/fmap (uuid/random-uuid) (gen/return nil))  ;; Generate UUIDs
    :name (gen/string-alphanumeric)))  ;; Generate random names

(deftest entity-properties-test
  (let [prop (prop/for-all [entity entity-gen]
                           (and (uuid? (:id entity))      ;; Ensure ID is a UUID
                                (string? (:name entity))  ;; Ensure Name is a string
                                (not (empty? (:name entity)))))]  ;; Ensure Name is non-empty
    (is (:result (tc/quick-check 100 prop))))) ;; Run the test 100 times
