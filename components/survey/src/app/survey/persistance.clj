(ns app.survey.persistance
  (:require [app.storage.interface :as storage]))

(def schema [{:db/ident :question/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/text
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/order
              :db/valueType :db.type/number
              :db/cardinality :db.cardinality/one}
             {:db/ident :question/active
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/email
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :survey/submitted-at
              :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/id
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/answer
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/survey
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :response/question
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}])

(def questions
  [["Besitzen Sie mehrere Mietobjekte?" 1 1]
   ["Haben Sie Schwierigkeiten, Mietzahlungen nachzuverfolgen?" 2 1]
   ["Verwenden Sie derzeit Excel zur Verwaltung Ihrer Immobilien?" 3 1]
   ["Ist das Beantworten von Mieteranfragen zeitaufwändig?" 4 1]
   ["Fällt es Ihnen schwer, den Überblick über Wartungsarbeiten zu behalten?" 5 1]
   ["Machen Sie sich Sorgen über die Einhaltung deutscher Mietgesetze?" 6 1]
   ["Verwalten Sie Ihre Mietobjekte aus der Ferne?" 7 1]
   ["Haben Sie Probleme mit regelmäßigen Finanzberichten?" 8 1]
   ["Möchten Sie die Kommunikation mit Mietern automatisieren?" 9 1]
   ["Haben Sie Schwierigkeiten bei der Verwaltung von Nebenkostenabrechnungen?" 10 1]
   ["Erstellen Sie Mietverträge manuell?" 11 1]
   ["Haben Sie ein System zur Mieterauswahl?" 12 1]
   ["Haben Sie mit Leerstandsquoten Ihrer Immobilien zu kämpfen?" 13 1]
   ["Fällt Ihnen die Steuerdokumentation für Ihre Immobilien schwer?" 14 1]
   ["Würden Sie von automatischen Zahlungserinnerungen profitieren?" 15 1]
   ["Haben Sie ein System zur Bearbeitung von Wartungsanfragen?" 16 1]
   ["Interessieren Sie sich für die Analyse der Leistung Ihrer Immobilien?" 17 1]
   ["Empfinden Sie den Abgleich von Kontoauszügen als mühsam?" 18 1]
   ["Möchten Sie den Verwaltungsaufwand bei der Immobilienverwaltung reduzieren?" 19 1]
   ["Suchen Sie nach besseren Möglichkeiten zur Verwaltung von Immobiliendokumenten?" 20 1]])


(defn transact-schema []
  (storage/transact-schema schema "surveys"))

(defn create-questions []
  (let [tx-data (mapv (fn [[text order active]]
                {:question/id (str (java.util.UUID/randomUUID))
                 :question/text text
                 :question/order order
                 :question/active (pos? active)}) ;; 1 -> true, 0 -> false
              questions)]
    (storage/transact tx-data "surveys")))

(defn list-questions []
  (let [data (storage/query "[:find ?id ?text ?order :where [?e :question/id ?id] [?e :question/text ?text] [?e :question/order ?order]]" "surveys")]
    (map (fn [[id text order]]
           {:id id :text text :order order})
         data)))

(defn store-survey-responses [data]
  (println "Responses: " data)
  #_(let [survey-id (str (java.util.UUID/randomUUID)) 
        survey-data [{:survey/id survey-id 
                      :survey/email (:email data)
                      :survey/submitted-at (java.util.Date.)}]
        responses-data (mapv (fn [[order answer]]
                               {:response/id (str (java.util.UUID/randomUUID))
                                :response/survey [:survey/id survey-id]
                                :response/question [:question/order (Integer/parseInt (name order))]  ;; lookup ref by order number
                                :response/answer answer})
                             (:responses data))]
    (storage/transact survey-data "surveys")
    (storage/transact responses-data "surveys")))

(comment
  "Test listing the questions"
  (list-questions))

(comment
  "Store the schema, for the moment let's do it manually"
  (storage/transact schema "surveys")
  )