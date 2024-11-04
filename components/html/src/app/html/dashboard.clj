(ns app.html.dashboard
  (:require [app.html.invoice-data :refer [html]]))

(defn content [{:keys [email created-at]}]
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
         [:a
          {:href "#",
           :class
           "rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white",
           :aria-current "page"}
          "Dashboard"]
         [:a
          {:href "#",
           :class
           "rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
          "Team"]
         [:a
          {:href "#",
           :class
           "rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
          "Projects"]
         [:a
          {:href "#",
           :class
           "rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
          "Calendar"]
         [:a
          {:href "#",
           :class
           "rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
          "Reports"]]]]
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
            :class
            "relative flex max-w-xs items-center rounded-full bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800",
            :id "user-menu-button",
            :aria-expanded "false",
            :aria-haspopup "true"}
           [:span {:class "absolute -inset-1.5"}]
           [:span {:class "sr-only"} "Open user menu"]
           [:img
            {:class "h-8 w-8 rounded-full",
             :src
             "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
             :alt ""}]]]
         (comment
           "Dropdown menu, show/hide based on menu state.\n\n                Entering: \"transition ease-out duration-100\"\n                  From: \"transform opacity-0 scale-95\"\n                  To: \"transform opacity-100 scale-100\"\n                Leaving: \"transition ease-in duration-75\"\n                  From: \"transform opacity-100 scale-100\"\n                  To: \"transform opacity-0 scale-95\"")
         [:div
          {:class
           "absolute right-0 z-10 mt-2 w-48 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none",
           :role "menu",
           :aria-orientation "vertical",
           :aria-labelledby "user-menu-button",
           :tabindex "-1"}
          (comment "Active: \"bg-gray-100\", Not Active: \"\"")
          [:a
           {:href "#",
            :class "block px-4 py-2 text-sm text-gray-700",
            :role "menuitem",
            :tabindex "-1",
            :id "user-menu-item-0"}
           "Your Profile"]
          [:a
           {:href "#",
            :class "block px-4 py-2 text-sm text-gray-700",
            :role "menuitem",
            :tabindex "-1",
            :id "user-menu-item-1"}
           "Settings"]
          [:a
           {:href "#",
            :class "block px-4 py-2 text-sm text-gray-700",
            :role "menuitem",
            :tabindex "-1",
            :id "user-menu-item-2"}
           "Sign out"]]]]]
      [:div
       {:class "-mr-2 flex md:hidden"}
       (comment "Mobile menu button")
       [:button
        {:type "button",
         :class
         "relative inline-flex items-center justify-center rounded-md bg-gray-800 p-2 text-gray-400 hover:bg-gray-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800",
         :aria-controls "mobile-menu",
         :aria-expanded "false"}
        [:span {:class "absolute -inset-0.5"}]
        [:span {:class "sr-only"} "Open main menu"]
        (comment "Menu open: \"hidden\", Menu closed: \"block\"")
        [:svg
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
        [:svg
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
    [:div
     {:class "md:hidden", :id "mobile-menu"}
     [:div
      {:class "space-y-1 px-2 pb-3 pt-2 sm:px-3"}
      (comment
        "Current: \"bg-gray-900 text-white\", Default: \"text-gray-300 hover:bg-gray-700 hover:text-white\"")
      [:a
       {:href "#",
        :class
        "block rounded-md bg-gray-900 px-3 py-2 text-base font-medium text-white",
        :aria-current "page"}
       "Dashboard"]
      [:a
       {:href "#",
        :class
        "block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
       "Team"]
      [:a
       {:href "#",
        :class
        "block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
       "Projects"]
      [:a
       {:href "#",
        :class
        "block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
       "Calendar"]
      [:a
       {:href "#",
        :class
        "block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700 hover:text-white"}
       "Reports"]]
     [:div
      {:class "border-t border-gray-700 pb-3 pt-4"}
      [:div
       {:class "flex items-center px-5"}
       [:div
        {:class "flex-shrink-0"}
        [:img
         {:class "h-10 w-10 rounded-full",
          :src
          "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
          :alt ""}]]
       [:div
        {:class "ml-3"}
        [:div
         {:class "text-base font-medium leading-none text-white"}
         "Tom Cook"]
        [:div
         {:class "text-sm font-medium leading-none text-gray-400"}
         "tom@example.com"]]
       [:button
        {:type "button",
         :class
         "relative ml-auto flex-shrink-0 rounded-full bg-gray-800 p-1 text-gray-400 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800"}
        [:span {:class "absolute -inset-1.5"}]
        [:span {:class "sr-only"} "View notifications"]
        [:img
         {:class "h-8 w-8 rounded-full",
          :src
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAAsTAAALEwEAmpwYAAAE3klEQVR4nO2a24vVVRTHP+OoM17IGZ0ms9LulJfpgn9CkQpTGr2UBUGUPdVMPfQQpBARZi/VUPRST5HUS2Z3JEaILK28PNSkU1FeKKx5CJpRx/HEgu+GxZk5v9/e+5wzSfiFwznwW9+192/vtdZea+0DF/D/RQdwJ/ACsBP4ATgJjOljv78H3pdML7CA8wRtwEbgE+AsUEn8GOdj4D7pmna0A08Ax92kTgGDwGZgA7ACWCTZdv1eCdwtmd3Aacc/BvRJdlqwFhh2E/gW2AR0ZujqFPc7p+8IcAdNhK3Uq25AG3xNg3S3aIEOOP2vNMPcLgG+0QCjwONAa6MHAWYC/QoONtZeoLtRyq/UdpviIWBVifx1wDPALuCEfOcfRbGXgGURY94EHNaYhzWHuncivMRXctgie38TmCiJVH8DDwAXlYzdBXztXqa7Hp8I5rQHmF8ge6lWPEz0Ddn8DcBcfSxiPe/C9Jh2yJ7Vwnz3MntzfeY1Z06LShx10EWwpSV6H9S5c0acL0om2OXMbCD1JdY5xy7zibske7LkhauxUmeHcZ8qkb3ZBYDo0DwH+EmkxyLk35KsHZCp6BX31wjZfucvUSb2pDsnYkLsX5K/hnS0aCeNvyQiNB+UrIX/Ugc/nrCF85yD5+Jz6bg9weSPle3K/RK0aBWDKyR/lHzslA4zs5gdDOnMvUWCn0no4chJdLsVykUYMzbdeVTyHxUdaGd1ElttEYNWHYBntFo5COdPT6T8QmXN47XqmfVSaDabgt/Eu4x0zNMinFa0jMXuInN8UQ8tT0rBe+LZeZKK28T9MpG3RTyrNCfhg8wJPSeefafiWXEtVUnBBvGsbJ6EkAbcmKBwhiuyniYdYWWHpSsWK1z6NAl/6mFKmrFEnBHyMSIdlnjGosulRZMQ6ufZCQpnKx+bSIh01ZFyQjpmJfDaXJ+gIS+CuiAV+djqBN5q55c1z4ScFwmmZXE6BbcAf4i7NYG3VZzfpSMFhaYVnN2KoVTcKq41D2IREsDUlzAsL3L23PCLIs5R8a0yLMNal77nZATrxbc8rWEHYkCf+D+XOH6HZCri5GBz0YGYm6IEzHL19ZRbLgy5GjwlUnkMFqUoOUljrbTeQmothC6LyeZgYVnS6FPqRzIHmSv+uQKZc5Ip6p4UYVNMyA6FlRUvOVjgWj21cEoyOVcKLa6wsg5+Yal7IrHQ8bhWXDuTaiGcVyabinWxpa5vPhxQwR+Ly9WNrCi1r4UdrulnnFjMBA7FNh+q20Ex4XGVGmejLvwWOfJSF35Hxe1pRjvIb+FYjUG6tSr7XE/XotH2yBTHZN6u6hPv12QX12hqJzfoqlumPyq3CR2/7Qp9YQIjWlWrEVJhnJddb6yiI+BdpT1o7CO5LdOpmtgDbgXHVZ3d06DLmDZdye1wizShi6U99TaxDRdrR7z5vANcT/OwDHi9ateHdcVRF652zv9LQtumHvS4gGAvcVWjFC/WlUFFt0/9Tbp6a1UzfNSZU907MZXPhAAQTv9G3r5aar/f6R9o9t37VNfTD2Ummh3iVl9Pr2GaMEcmENKZijqGu9Ti6VVbqVNF1wz9Xq5nWyQbbq0qugXom84/DHi0K9H8tM6/cGz8r/7CUctMrEzeBnyoAmpEYXRcv4f0bJtkc+ueC+B8x78fHcKd4U6+1gAAAABJRU5ErkJggg==",
          :alt ""}]]]
      [:div
       {:class "mt-3 space-y-1 px-2" :style "display: none;"}
       [:a
        {:href "#",
         :class
         "block rounded-md px-3 py-2 text-base font-medium text-gray-400 hover:bg-gray-700 hover:text-white"}
        "Your Profile"]
       [:a
        {:href "#",
         :class
         "block rounded-md px-3 py-2 text-base font-medium text-gray-400 hover:bg-gray-700 hover:text-white"}
        "Settings"]
       [:a
        {:href "#",
         :class
         "block rounded-md px-3 py-2 text-base font-medium text-gray-400 hover:bg-gray-700 hover:text-white"}
        "Sign out"]]]]]
   [:header
    {:class "bg-white shadow"}
    [:div
     {:class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"}
     [:h1
      {:class "text-3xl font-bold tracking-tight text-gray-900"}
      "Dashboard"]]]
   [:main
    [:div
     {:class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"}
     (html)]]])