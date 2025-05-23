(ns app.html.layout 
  (:require
   [app.html.new-feature :as new-feature]))

(def menu-id {:main-menu-1 "Dashboard" 
              :main-menu-2 "Buildings" 
              :main-menu-3 "Properties"
              :main-menu-4 "Bank accounts"
              :main-menu-5 "Reports" 
              :sec-menu-1 "Your Profile"
              :sec-menu-2 "Settings"
              :sec-menu-3 "Sign out"})

(def ^{:private true} menu-config
  "Configuration for the navigation bar and user profile menus."
   {:navbar {:menu [{:name (:main-menu-1 menu-id ) :href "#"} 
                    {:name (:main-menu-2 menu-id ) :href "/user-buildings"} 
                    {:name (:main-menu-3 menu-id ) :href "/properties"} 
                    {:name (:main-menu-4 menu-id ) :href "/bank"} 
                    {:name (:main-menu-5 menu-id ) :href "#"}]} 
    :profile {:menu [{:name (:sec-menu-1 menu-id ) :href "#"} 
                     {:name (:sec-menu-2 menu-id ) :href "#"} 
                     {:name (:sec-menu-3 menu-id ) :href "/sign-in"}]}})

(defn- load-profile-menu
  "Renders the user profile menus.\n
   Args:\n
   \tmobile: (true, false) indicates whether the user is in landscape or portrait mode.\n
   Returns: Hiccup user profile menus from configuration."
  [mobile]
  (let [profile-menu (:menu (:profile menu-config))]
    (for [menu profile-menu]
      [:a
       (if mobile
         {:href (menu :href)
          :class "block rounded-md px-3 py-2 text-base font-medium text-gray-400 hover:bg-gray-700 hover:text-white"}
         {:href (menu :href)
          :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          :role "menuitem"})
       (menu :name)])))

(defn- load-navbar-menu
  "Renders the navigation bar menus.\n
   Args:\n
   \tmobile: (true, false) indicates whether it is in landscape or portrait mode.\n
   \tselected: is the name of the selected menu\n
   Returns: Hiccup navigation bar menus from configuration."
  [mobile selected]
  (let [navbar-menu (:menu (:navbar menu-config))]
    (for [menu navbar-menu]
      [:a
       {:href (menu :href),
        :class
        (if mobile
          (if (= (menu :name) selected)
            "block rounded-md bg-gray-900 px-3 py-2 text-base font-medium text-white"
            "block rounded-md px-3 py-2 text-base font-medium text-white hover:bg-gray-700 hover:text-gray-500")
          (if (= (menu :name) selected)
            "rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white"
            "rounded-md px-3 py-2 text-sm font-medium text-white hover:bg-gray-700 hover:text-gray-300"))
        :aria-current "page"}
       (menu :name)])))

(defn content
  "Renders the main page content.\n
   Args:\n
   \temail: from current session,\n
   \tcreated-at: date from current session,\n
   \tcontent: is a map like {:title \"\" :content [...] :menu-id \"Dashboard\"}\n
   where :title is the nav bar title, :content is the page main section content\n
   and :menu-id is the name of the menu to select.\n
   Returns: Hiccup page content.
   "
  [{:keys [email created-at content]}] 
  [:div
   {:class "min-h-full"}
   [:nav
    {:class "bg-gray-800"}
    [:div
     {:class "mx-auto max-w-7xl px-4 sm:px-6 lg:px-8"}
     [:div
      {:class "flex h-16 items-center justify-between"}
      [:div
       {:class "flex items-center"}
       [:div
        {:class "hidden md:block"}
        [:div
         {:class "ml-10 flex items-baseline space-x-4"}
         (comment
           "Current: \"bg-gray-900 text-white\", Default: \"text-gray-300 hover:bg-gray-700 hover:text-white\"")
         (load-navbar-menu false (:menu-id content))]]]
      [:div
       {:class "hidden md:block"}
       [:div
        {:class "ml-4 flex items-center md:ml-6"}
        [:button
         {:type "button",
          :class
          "relative rounded-full bg-gray-800 p-1 text-gray-400 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800"}
         [:span {:class "absolute -inset-1.5"}]
         [:span {:class "sr-only"} "View notifications"]
         [:svg
          {:class "h-6 w-6",
           :fill "none",
           :viewBox "0 0 24 24",
           :stroke-width "1.5",
           :stroke "currentColor",
           :aria-hidden "true"}
          [:path
           {:stroke-linecap "round",
            :stroke-linejoin "round",
            :d
            "M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"}]]]
        (comment "Profile dropdown")
        [:div
         {:class "relative ml-3"}
         [:div
          [:button
           {:type "button",
            :_ "on click toggle .hidden on #dropdown-menu
                on blur wait 180ms then toggle .hidden on #dropdown-menu",;;Hyperscript code to control landscape dropdown menu
            :class
            "relative flex max-w-xs items-center rounded-full bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800",
            :aria-expanded "false",
            :aria-haspopup "true"}
           [:span {:class "absolute -inset-1.5"}]
           [:span {:class "sr-only"} "Open user menu"]
           [:img
            {:class "h-8 w-8 rounded-full",
             :src "def-building.svg",
             :alt ""}]]]
         (comment
           "Dropdown menu, show/hide based on menu state.\n\n Entering: \"transition ease-out duration-100\"\n From: \"transform opacity-0 scale-95\"\n                  To: \"transform opacity-100 scale-100\"\n                Leaving: \"transition ease-in duration-75\"\n                  From: \"transform opacity-100 scale-100\"\n                  To: \"transform opacity-0 scale-95\"")
         [:div#dropdown-menu
          {:class
           "hidden absolute right-0 z-10 mt-2 w-48 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none",
           :role "menu",
           :aria-orientation "vertical",
           :aria-labelledby "user-menu-button",
           :tabindex "-1"}
          (comment "Active: \"bg-gray-100\", Not Active: \"\"")
          (load-profile-menu false)]]]]
      [:div
       {:class "-mr-2 flex md:hidden"}
       (comment "Mobile menu button")
       [:button
        {:type "button",
         :_ "on click toggle .hidden on #mobile-menu
             then toggle .hidden on #svg-v
             then toggle .hidden on #svg-h
             on blur wait 180ms 
             then toggle .hidden on #mobile-menu
             then toggle .hidden on #svg-v
             then toggle .hidden on #svg-h",;;Hyperscript code to control portrait dropdown menu
         :class
         "relative inline-flex items-center justify-center rounded-md bg-gray-800 p-2 text-gray-400 hover:bg-gray-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800",
         :aria-controls "mobile-menu",
         :aria-expanded "false"}
        [:span {:class "absolute -inset-0.5"}]
        [:span {:class "sr-only"} "Open main menu"]
        (comment "Menu open: \"hidden\", Menu closed: \"block\"")
        [:svg#svg-v
         {:class "block h-6 w-6",
          :fill "none",
          :viewBox "0 0 24 24",
          :stroke-width "1.5",
          :stroke "currentColor",
          :aria-hidden "true"}
         [:path
          {:stroke-linecap "round",
           :stroke-linejoin "round",
           :d "M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"}]]
        (comment "Menu open: \"block\", Menu closed: \"hidden\"")
        [:svg#svg-h
         {:class "hidden h-6 w-6",
          :fill "none",
          :viewBox "0 0 24 24",
          :stroke-width "1.5",
          :stroke "currentColor",
          :aria-hidden "true"}
         [:path
          {:stroke-linecap "round",
           :stroke-linejoin "round",
           :d "M6 18L18 6M6 6l12 12"}]]]]]]
    (comment "Mobile menu, show/hide based on menu state.")
    [:div#mobile-menu;;Menu mobile (Portrait)
     {:class "hidden md:hidden"}
     [:div
      {:class "space-y-1 px-2 pb-3 pt-2 sm:px-3"}
      (comment
        "Current: \"bg-gray-900 text-white\", Default: \"text-gray-300 hover:bg-gray-700 hover:text-white\"")
      (load-navbar-menu true (:menu-id content))]
     [:div
      {:class "border-t border-gray-700 pb-3 pt-4"}
      [:div
       {:class "flex items-center px-5"}
       [:div
        {:class "flex-shrink-0"}
        [:img
         {:class "h-10 w-10 rounded-full",
          :src "def-building.svg",
          :alt ""}]]
       [:div
        {:class "ml-3"}
        [:div
         {:class "text-base font-medium leading-none text-white"}
         (str "Create at: " created-at)]
        [:div
         {:class "text-sm font-medium leading-none text-gray-400"}
         email]]
       [:button
        {:type "button",
         :class
         "relative ml-auto flex-shrink-0 rounded-full bg-gray-800 p-1 text-gray-400 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800"}
        [:span {:class "absolute -inset-1.5"}]
        [:span {:class "sr-only"} "View notifications"]
        [:svg
         {:class "h-6 w-6",
          :fill "none",
          :viewBox "0 0 24 24",
          :stroke-width "1.5",
          :stroke "currentColor",
          :aria-hidden "true"}
         [:path
          {:stroke-linecap "round",
           :stroke-linejoin "round",
           :d
           "M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"}]]]]
      [:div
       {:class "mt-3 space-y-1 px-2"}
       (load-profile-menu true)]]]] 
   [:header
    {:class "bg-white shadow"}
    [:div {:class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"}
     [:div {:class "flex items-center justify-between"} 
      [:h1 {:class "text-3xl font-bold tracking-tight text-gray-900"} 
        (:title content)]]]
    (new-feature/get-new-feature-form)]
[:main
 [:div
  {:class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"}
     [:div#dashboard-dyn-ctn (:content content)]]]])