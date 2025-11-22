(ns app.web.core
  (:require [integrant.core :as ig]
            #_[reagent.dom :as rdom] 
            [re-frame.core :as re-frame]
            [reagent.core :as r]
            ["react-dom/client" :as rdom]
            [app.web.events :as events] ))

(defonce root (rdom/createRoot (.getElementById js/document "app")))
  
(defn ^:dev/after-load mount-root [main-component]
  (re-frame/clear-subscription-cache!)
  (let [file-upload-component main-component]
    (.render root (r/as-element [file-upload-component]))))
  
(defn init [main-component]
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root main-component))

