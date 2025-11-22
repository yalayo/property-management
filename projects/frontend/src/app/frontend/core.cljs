(ns app.frontend.core
  (:require [integrant.core :as ig]
            [app.frontend.interceptors :as interceptors]
            [app.web.core :as web]
            [app.upload-ui.interface :as upload]
            [app.user-ui.interface :as user]
            [app.platform-ui.interface :as platform]))

(def config
  {::interceptors/storage {}
   ::upload/component {}
   ::user/component {:storage-interceptor (ig/ref ::interceptors/storage)}
   ::platform/component {:user-component (ig/ref ::user/component)}})

(defonce system (atom nil))

(defn start []
  (reset! system (ig/init config)))

(defn stop []
  (when @system
    (ig/halt! @system)
    (reset! system nil)))

(defn restart []
  (stop)
  (start))

(defn init-upload []
  (start)
  (let [file-upload-component (::upload/component @system)]
    (web/init file-upload-component)))

(defn init-platform []
  (start)
  (let [platform-component (::platform/component @system)]
    (web/init platform-component)))