(ns app.user.persistance
  (:require [server.db :as db]            ;; your db wrapper
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [server.cf :as cf]
            [lib.async :refer [js-await]]
            ["bcryptjs" :as bcrypt]
            ["uuid" :as uuid]))         ;; npm install uuid

#_(defn hash-password [pw]
  (.hashSync bcrypt pw 10)) ;; 10 salt rounds

#_(defn create-account [email password]
  (let [user-id (.v4 uuid)               ;; generate a uuid
        hashed  (hash-password password)
        query   (-> (h/insert-into :accounts)
                    (h/columns :user_id :email :password)
                    (h/values [[user-id email hashed]])
                    (sql/format))]
    (db/run+ query)))

;; Wrap bcrypt.hash in a Promise so we can await it
(defn hash-password [pw]
  (js/Promise.
    (fn [resolve reject]
      ;; 10 salt rounds
      (.hash bcrypt pw 10
             (fn [err hashed]
               (if err
                 (reject err)
                 (resolve hashed)))))))

(defn create-account [email password]
  (let [user-id (.v4 uuid)]
    (js-await [hashed (hash-password password)]
      (let [query (-> (h/insert-into :accounts)
                      (h/columns :user_id :email :password)
                      (h/values [[user-id email hashed]])
                      (sql/format))]
        (js-await [{:keys [success results]} (db/run+ query)]
          (if success
            (cf/response-edn {:result results} {:status 200})
            (cf/response-error)))))))
