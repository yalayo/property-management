(ns app.frontend.platform.views
  (:require [re-frame.core :as re-frame]
            [app.frontend.platform.subs :as subs]
            [app.frontend.user.views :as user-view]
            [app.frontend.dashboard.views :as dashboard]))

(defn app []
  (let [user-loged-in? @(re-frame/subscribe [::subs/logged-in])]
    [:<>
     (if user-loged-in?
       (dashboard/dashboard-component)
       (user-view/login-component))]))