(ns app.upload-ui.interface
  (:require [integrant.core :as ig]
            [app.upload-ui.views :as views]))

(defmethod ig/init-key ::component [_ {:keys []}]
  views/access-component)
