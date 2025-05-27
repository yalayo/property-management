(ns testcontainers
  (:require [clj-test-containers.core :as tc]
            [etaoin.api :as etaoin]))

(let [webdriver-port 4444
      container (-> (tc/create {:image-name "selenium/standalone-chromium:131.0"
                                :exposed-ports [webdriver-port]})
                    (tc/start!))
      driver (etaoin/chrome-headless {:port (get (:mapped-ports container) webdriver-port)
                                      :host (:host container)
                                      :args ["--no-sandbox"]})]

  (etaoin/go driver "https://clojure.org")
  (etaoin/visible? driver {:tag :h2
                           :fn/has-text "The Clojure Programming Language"}))  