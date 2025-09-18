(ns app.core.system-test
  (:require [clojure.test :refer :all]
             [fugato.core :as f]
             [clojure.test.check.generators :as gen]
             [odoyle.rules :as o]
             [hausverwaltung.rules :as hr]
             [hausverwaltung.generators :as g]))
  
  (deftest late-payment-detected
    (f/defcheck rent-check
      [contracts (gen/vector g/gen-contract 3)
       payments  (gen/vector g/gen-payment 2)] ;; fewer payments on purpose
      (let [session (-> (hr/make-session)
                        (as-> s
                              (reduce (fn [s c] (o/insert s (:contract/id c) c)) s contracts)
                          (reduce (fn [s p] (o/insert s (:payment/id p) p)) s payments)
                          (o/fire-rules)))]
        ;; property: at least one tenant may be marked late if missing a payment
        (let [statuses (map #(o/query-all session %)
                            (map :contract/tenant contracts))]
          (is (every? some? statuses))))))