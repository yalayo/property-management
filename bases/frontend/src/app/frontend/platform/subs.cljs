(ns app.frontend.platform.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::logged-in
 (fn [db [_ _]]
   (:user-loged-in? db)))