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

#_(defn ^js/Promise run+ [query]
  ;; Wrap in a JS async function
  (js/Promise.
   (fn [resolve reject]
     (let [async-fn (fn []
                      (js/console.log "Preparing query...")
                      (let [[sql & args] (sql/format query)
                            stmt (.prepare ^js @cf/DB sql)
                            bound-stmt (.apply (.-bind stmt) stmt (clj->js args))]
                        (js/console.log "SQL:" sql)
                        (js/console.log "Bound statement params:" (.-params bound-stmt))
                        ;; Await the run
                        (-> (.run bound-stmt)
                            (.then (fn [result]
                                     (js/console.log "Insert result meta:" result)
                                     (js/console.log "Insert result meta:" (.-meta ^js result))
                                     #_(resolve (js->clj result :keywordize-keys true))))
                            (.catch (fn [err]
                                      (js/console.error "DB Error:" err)
                                      (reject err))))))]
       ;; Call the async function immediately
       (async-fn)))))

#_(defn ^js/Promise run+ [query]
  (let [[sql & args] (sql/format query)
        stmt (.prepare ^js @cf/DB sql)
        bounded (.-bind stmt)]
    (js/console.log "Parameters:" (clj->js args))
    (js/console.log "Apply:"  (.apply bounded stmt (clj->js args)))
    (js-await [result (.run ^js (.apply bounded stmt (clj->js args)))]
              result)))

(defn ^js/Promise run+ [query]
  (js/Promise.
   (fn [resolve reject]
     (try
       (let [[sql & params] (sql/format query)
             jsparams (into-array (cons nil params))
             stmt (.prepare ^js @cf/DB sql)]
         
         (js/console.log "Before:" jsparams)

         
         ;; D1 requires: stmt.bind(param1, param2, param3)
         ;; apply() requires a dummy first arg which is ignored: nil / null
         #_(.unshift jsparams nil)

         (js/console.log "SQL:" (sql/format query))
         (js/console.log "After:" jsparams)

         (let [bound (.call (.-apply js/Function.prototype)
                                  (.-bind stmt)   ;; function to apply
                                  stmt            ;; `this` value
                                  jsparams)]
           (js/console.log "Bounded:" bound)
           (-> (.run bound)
               (.then (fn [res]
                        (js/console.log "Response:" res)
                        (resolve res)))
               (.catch (fn [err]
                         (js/console.error "D1 RUN ERROR:" err)
                         (reject err)
                         (throw err)))))
         )

       (catch :default e
         (js/console.error "D1 PREPARE/BIND ERROR:" e)
         (reject e)
         (throw err))))))

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
