(ns app.platform-ui.views
  (:require [re-frame.core :as re-frame]
            [app.frontend.config :as config]
            [app.platform-ui.subs :as subs]))

(defn platform-component [user-component]
  (let [user-loged-in? @(re-frame/subscribe [::subs/logged-in])]
    [:<>
     (if (config/internal?)
       [:div "Temp dashboard place holder"]
       (if user-loged-in?
         [:div "Temp dashboard place holder"]
         user-component))]))