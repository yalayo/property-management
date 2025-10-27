(ns portal
  (:require [portal.api :as p]))

(def p (p/open {:launcher :vs-code :editor :vs-code}))
(add-tap #'p/submit)