(ns app.user.routes)

(def routes [["/sign-in" :post-sign-in]
             ["/sign-up" :post-sign-up]])


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