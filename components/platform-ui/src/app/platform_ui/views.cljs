(ns app.platform-ui.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [app.frontend.config :as config]
            [app.frontend.platform.subs :as subs]
            [app.frontend.user.views :as user-view]
            [app.frontend.dashboard.views :as dashboard]))

(defn app []
  (let [user-loged-in? @(re-frame/subscribe [::subs/logged-in])]
    [:<>
     (if (config/internal?)
       (dashboard/dashboard-component)
       (if user-loged-in?
         (dashboard/dashboard-component)
         (user-view/access-component)))]))