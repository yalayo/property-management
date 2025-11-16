(ns app.upload-ui.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::email
 (fn [db]
   (get-in db [:upload :data :email])))