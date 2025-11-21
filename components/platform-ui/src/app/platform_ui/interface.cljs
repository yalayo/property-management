(ns app.platform-ui.interface
  (:require [integrant.core :as ig]
            [app.platform-ui.views :as views]))

(defmethod ig/init-key ::component [_ {:keys []}]
  views/file-upload-component)
