(ns app.web.core
  (:require [integrant.core :as ig]
            #_[reagent.dom :as rdom] 
            [re-frame.core :as re-frame]
            [reagent.core :as r]
            ["react-dom/client" :as rdom]
            [app.upload-ui.interface :as upload]
            [app.web.events :as events] ))

(def config 
  {::upload/component {}})

(defonce system (atom nil))
(defonce root (rdom/createRoot (.getElementById js/document "app")))

(defn start []
  (reset! system (ig/init config)))

(defn stop []
  (when @system
    (ig/halt! @system)
    (reset! system nil)))

(defn restart []
  (stop)
  (start))
  
(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [file-upload-component (::upload/component @system)]
    (.render root (r/as-element [file-upload-component]))))
  
(defn init []
  (start)
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))

