(ns app.worker.db
	(:require [app.worker.async :refer [js-await]]
			  [app.worker.cf :as cf]
			  [honey.sql :as sql]))

;; D1 docs https://developers.cloudflare.com/d1/

(defn ^js/Promise query+ [query-map]
  (let [[sql params] (sql/format query-map)
        stmt (.prepare ^js @cf/DB sql)]
    (js-await [result (.all (.apply (.-bind stmt) stmt (to-array params)))]
              {:success true :account (js->clj (first (.-results result)) :keywordize-keys true)})))

#_(defn ^js/Promise run+ [query-map]
  (console.log "Query: " query-map)
  (let [[query & params] (sql/format query-map)
        stmt (.prepare ^js @cf/DB query)]
    (console.log "Processed: " query) 
    (console.log "Parameters: " (to-array params))
    (js-await [result (.run (.apply (.-bind stmt) stmt (to-array params)))]
              (js->clj result :keywordize-keys true))))

#_(defn ^js/Promise run+ [query-map]
  (let [[query & params] (sql/format query-map)
        stmt (.prepare ^js @cf/DB query)
        ;; flatten first row and convert to JS array
        js-params (-> params clj->js js/Array.from)]
    (js/console.log "Processed: " query)
    (js/console.log "Parameters: " js-params)
    (js-await [result (.run (.apply (.-bind stmt) stmt js-params))]
              (js->clj result :keywordize-keys true))))

#_(defn ^js/Promise run+ [query]
  (let [[query & args] (sql/format query)
        stmt (.prepare ^js @cf/DB query)]
    (js/console.log "Parameters: " (into-array args))
    (js-await [result (.run (.apply (.-bind stmt) stmt (into-array args)))]
              (js->clj result :keywordize-keys true))))
;; Check later
#_(defn ^js/Promise run+ [query-map]
  (let [[sql params] (sql/format query-map)]
    (js-await [stmt (.prepare ^js @cf/DB sql)]
              (js-await [result (.run ^js (.bind stmt (clj->js params)))]
                        (js->clj result :keywordize-keys true)))))

(defn ^js/Promise run+ [query]
  ;; Wrap in a JS async function
  (js/Promise.
   (fn [resolve reject]
     (let [async-fn (fn []
                      (js/console.log "Preparing query...")
                      (let [[sql & args] (sql/format query)
                            stmt (.prepare ^js @cf/DB sql)
                            bound-stmt (.apply (.-bind stmt) stmt (into-array ["user-id" "email@mail.com" "password"]))]
                        (js/console.log "SQL:" sql)
                        (js/console.log "Bound statement params:" (.-params bound-stmt))
                        ;; Await the run
                        (-> (.run bound-stmt)
                            (.then (fn [result]
                                     (js/console.log "Insert result meta:" (.-meta ^js result))
                                     (resolve (js->clj result :keywordize-keys true))))
                            (.catch (fn [err]
                                      (js/console.error "DB Error:" err)
                                      (reject err))))))]
       ;; Call the async function immediately
       (async-fn)))))


#_(defn ^js/Promise run+ [query]
  (let [[sql & args] (sql/format query)
        stmt (.prepare ^js @cf/DB sql)
        bound-stmt (.apply (.-bind stmt) stmt (clj->js args))]
    (js/console.log "SQL:" sql)
    (js/console.log "Bound statement:" bound-stmt)

    (.then (.run bound-stmt)
           (fn [result]
             (js/console.log "Insert result:" result)
             ;; convert to CLJ if you want
             (js->clj result :keywordize-keys true)))

    #_(if (cf-production?)
      ;; -----------------------------------------
      ;; PRODUCTION: Cloudflare D1 (async API)
      ;; -----------------------------------------
      (js-await [result (.run (.apply (.-bind stmt) stmt (to-array args)))]
                (js->clj result :keywordize-keys true))

      ;; -----------------------------------------
      ;; LOCAL DEVELOPMENT: better-sqlite3 (sync API)
      ;; -----------------------------------------
      (js-await [result (.run (.apply (.-bind stmt) stmt (into-array args)))]
                (js->clj result :keywordize-keys true)))))
