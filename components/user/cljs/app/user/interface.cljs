(ns app.user.interface
  (:require [app.user.routes :as routes]))

(defn get-routes []
  routes/routes)