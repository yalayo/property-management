(ns app.frontend.platform.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [app.frontend.config :as config]
            [app.frontend.platform.subs :as subs]
            [app.frontend.user.views :as user-view]
            [app.frontend.dashboard.views :as dashboard]))

(defn home-page []
  (r/create-class
   {:component-did-mount #(re-frame/dispatch [:init-google])
    :reagent-render
    (fn []
      [:div
       [:h1 "Login with Google"]
       [:div#g-signin]])}))

(defn app []
  (let [user-loged-in? @(re-frame/subscribe [::subs/logged-in])]
    [:<>
     (if (config/internal?)
       (dashboard/dashboard-component)
       (if user-loged-in?
         (dashboard/dashboard-component)
         (user-view/access-component)))]))