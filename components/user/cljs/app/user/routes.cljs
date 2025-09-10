(ns app.user.routes)

(def routes [["/sign-in" {:post (fn [req] 
                                  (println "Test sign-in"))}]
             ["/sign-up" {:post (fn [req]
                                  (println "Test sign-up"))}]])


(comment 
  (def base-routes
  ["/api"
   ["/todos" ::todos]
   ["/todos/:id" ::todo]
   ["/presence" ::presence]])

(def together (concat routes routes-2))

together

(def all-routes
  (into base-routes together))

all-routes)