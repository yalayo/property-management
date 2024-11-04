(ns app.server.core
	(:require [app.web.interface :as web]))

(defn -main []
	(web/start))