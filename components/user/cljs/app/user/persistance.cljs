(ns app.user.persistance
  (:require [app.worker.db :as db]            ;; your db wrapper
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [app.worker.cf :as cf]
            [app.worker.async :refer [js-await]]))         ;; npm install uuid

#_(defn hash-password [pw]
  #_(.hashSync ^js bcrypt pw 10)) ;; 10 salt rounds

#_(defn create-account [email password]
  (let [user-id (.v4 uuid)               ;; generate a uuid
        hashed  (hash-password password)
        query   (-> (h/insert-into :accounts)
                    (h/columns :user_id :email :password)
                    (h/values [[user-id email hashed]])
                    (sql/format))]
    (db/run+ query)))

;; Wrap bcrypt.hash in a Promise so we can await it
(defn hash-password
  [password salt]
  (let [input (str salt ":" password)
        encoder (js/TextEncoder.)
        data (.encode encoder input)]
    (-> (js/Promise.resolve
         (.digest js/crypto.subtle "SHA-256" data))
        (.then (fn [hash-buffer]
                 (let [hash-array (js/Uint8Array. hash-buffer)]
                   (->> hash-array
                        (map (fn [b]
                               (.padStart (.toString b 16) 2 "0")))
                        (apply str))))))))

(defn create-account [email password]
  (let [user-id (js/crypto.randomUUID)]
    (js-await [hashed (hash-password password "temporary salt")]
      (let [query (-> (h/insert-into :accounts)
                      (h/columns :user_id :email :password)
                      (h/values [[user-id email hashed]])
                      (sql/format))]
        (js-await [{:keys [success results]} (db/run+ query)]
          (if success
            (cf/response-edn {:result results} {:status 200})
            (cf/response-error)))))))
