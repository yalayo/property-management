(ns app.frontend.dashboard.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-tab
 (fn [db]
   (get-in db [:dashboard :active-tab] "overview")))