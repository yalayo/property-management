(ns app.frontend.user.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            ["/pages/login$default" :as login]
            ["/pages/register$default" :as register]))

(def login-component (r/adapt-react-class login))
(def register-component (r/adapt-react-class register))