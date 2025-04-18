(ns app.frontend.platform.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [app.frontend.platform.events :as events]
            [app.frontend.platform.views :as views]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/app] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))