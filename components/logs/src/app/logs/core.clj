(ns app.logs.core
  (:require [com.brunobonacci.mulog.buffer :as rb]
            [clj-http.client :as http]))

(deftype TelegramPublisher [config buffer]

  com.brunobonacci.mulog.publisher.PPublisher
  (agent-buffer [_]
    buffer)

  (publish-delay [_]
    500)

  (publish [_ buffer]
    (let [bot-token (:bot-token config)
          chat-id (:chat-id config)]
      (doseq [item (map second (rb/items buffer))]
        (http/post (str "https://api.telegram.org/bot" bot-token "/sendMessage")
                   {:form-params {:chat_id chat-id
                                  :text (str "Event " (:mulog/event-name item) " - " (:message item))}
                    :content-type :json})))
    ;; return the buffer minus the published elements
    (rb/clear buffer)))


(defn telegram-publisher [config]
  (TelegramPublisher. config (rb/agent-buffer 10000)))