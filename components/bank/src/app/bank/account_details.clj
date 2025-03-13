(ns app.bank.account-details)

(defn content [id-account]
  [:div "Load content from database:" [:p (str "Account details. ID: " id-account)]])
