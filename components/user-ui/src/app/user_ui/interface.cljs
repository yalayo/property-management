(ns app.user-ui.interface
  (:require [integrant.core :as ig]
            [app.user-ui.events :as events]
            [app.user-ui.views :as views]))

(defmethod ig/init-key ::component [_ {:keys [storage-interceptor]}]
  (events/register-events! storage-interceptor)
  views/access-component)
