(ns server.core
	(:require ["cloudflare:workers" :refer [DurableObject]]
						[reitit.core :as r]
						[lib.async :refer [js-await]]
						[server.cf.durable-objects :as do]
						[server.db :as db]
						[server.cf :as cf :refer [defclass]]
						[server.schema :as schema]))

;; usage example of Durable Objects as a short-lived state
;; for user presence tracking in multiplayer web app
(defclass ^{:extends DurableObject} PresenceDurableObject [ctx env]
	Object
	(constructor [this ctx env]
		(super ctx env))

	(add-user-presence+ [this id timestamp]
		(js-await [_ (do/storage-put+ ctx id timestamp)
							 users (do/storage-list+ ctx)
							 now (js/Date.now)]
			(doseq [[id _] (->> (cf/js->clj users)
													(filter (fn [[id ts]] (> (- now ts) 10000))))]
				(do/storage-delete+ ctx id))
			(do/storage-list+ ctx))))

(def router
	(r/router
		["/api"
		 ["/todos" ::todos]
		 ["/todos/:id" ::todo]
		 ["/presence" ::presence]]))

;; args:
;;  route: Reitit route data
;;  request: js/Request object https://developers.cloudflare.com/workers/runtime-apis/request/
;;  env: Environment object containing env vars and bindings to Cloudflare services https://developers.cloudflare.com/workers/configuration/environment-variables/
;;  ctx: The Context API provides methods to manage the lifecycle of your Worker https://developers.cloudflare.com/workers/runtime-apis/context/
(defmulti handle-route (fn [route request env ctx]
												 [(-> route :data :name) (keyword (.-method ^js request))]))

(defmethod handle-route [::todos :GET] [route request env ctx]
	(js-await [{:keys [success results]} (db/query+ {:select [:*]
																									 :from   [:todo]})]
		(if success
			(cf/response-edn {:result results} {:status 200})
			(cf/response-error))))

(defmethod handle-route [::todos :POST] [route request env ctx]
	(js-await [{:keys [title description due_date status]} (cf/request->edn request)
						 todo {:id (str (random-uuid))
									 :title title
									 :description description
									 :due_date due_date
									 :status status}]
		(schema/with-validation {schema/NewTodo todo}
			:valid
			(fn []
				(js-await [{:keys [success results]} (db/run+ {:insert-into [:todo] :values [todo]})]
					(if success
						(cf/response-edn {:result results} {:status 200})
						(cf/response-error))))
			:not-valid
			(fn [errors]
				(cf/response-error errors)))))

(defmethod handle-route [::todo :POST] [route request env ctx]
	(js-await [{:keys [id title description due_date status]} (cf/request->edn request)
						 todo {:id id
									 :title title
									 :description description
									 :due_date due_date
									 :status status}]
		(schema/with-validation {schema/NewTodo todo}
			:valid
			(fn []
				(js-await [{:keys [success results]} (db/run+ {:update [:todo]
																											 :set     (dissoc todo :id)
																											 :where   [:= :id id]})]
					(if success
						(cf/response-edn {:result results} {:status 200})
						(cf/response-error))))
			:not-valid
			(fn [errors]
				(cf/response-error errors)))))

(defmethod handle-route [::todo :DELETE] [{:keys [path-params]} request env ctx]
	(let [{:keys [id]} path-params]
		(schema/with-validation {schema/TodoId id}
			:valid
			(fn []
				(js-await [{:keys [success results]} (db/run+ {:delete-from [:todo]
																											 :where        [:= :id id]})]
					(if success
						(cf/response-edn {:result results} {:status 200})
						(cf/response-error))))
			:not-valid
			(fn [errors]
				(cf/response-error errors)))))

(defmethod handle-route [::presence :GET] [{:keys [query-params] :as req} request env ctx]
	(let [{:keys [rid uid]} query-params
				presence-do (do/name->instance "DO_PRESENCE" rid)]
		(js-await [room (.add-user-presence+ presence-do uid (js/Date.now))
							 ;; only JS data types can go in/out of Durable Objects,
							 ;; so js->clj conversion has ot be done after the data is returned
							 ;; (Durable Objects is essentially a distributed state in Cloudflare's network,
							 ;; which means that data that's passed around has to be serialized/deserialized)
							 users (keys (cf/js->clj room))]
			(cf/response-edn {:result users} {:status 200}))))

;; entry point
(def handler
	#js {:fetch (cf/with-handler router handle-route)})