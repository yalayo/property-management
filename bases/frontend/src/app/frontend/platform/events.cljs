(ns app.frontend.platform.events
  (:require [re-frame.core :as re-frame :refer [after]]
            [cljs.reader]
            [app.frontend.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.http-fx]))

;; Initializing
;; Interceptors
(def ->local-store (after db/db->local-store))

;; Interceptor Chain
(def interceptors [->local-store])

;; To restore db from the browser's local storage
(re-frame/reg-cofx
 :local-store-db
 (fn [cofx _]
   (assoc cofx :local-store-db
						 ;; read in todos from localstore, and process into a sorted map
          (into (sorted-map)
                (some->> (.getItem js/localStorage db/ls-key)
                         (cljs.reader/read-string))))))

(re-frame/reg-event-fx
 ::initialize-db
 [(re-frame/inject-cofx :local-store-db)]
 (fn-traced [{:keys [local-store-db]} _]
            {:db local-store-db}))