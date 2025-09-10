(ns server.handler
  (:require
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [server.cf :as cf]
   [app.user.handler]))

(defmethod ig/init-key :server/handler
  [_ {:keys [router]}]
  ;; router -> ring-handler
  (let [ring-handler (ring/ring-handler router)]
    (fn [^js request env ctx]
      ;; cf/with-handler should take a Ring handler and adapt it
      ;; If you don’t have such a helper, write one:
      (cf/ring->fetch ring-handler request env ctx))))