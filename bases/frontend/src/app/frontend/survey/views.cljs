(ns app.frontend.survey.views
  (:require [reagent.core  :as r]
            [re-frame.core :as re-frame]
            [app.frontend.survey.subs :as survey-subs]
            [app.frontend.survey.events :as survey-events]
            ["/components/landing/Survey$default" :as survey-js]
            ["/pages/home$default" :as home]))

(def survey (r/adapt-react-class survey-js))
(def home-component (r/adapt-react-class home))

(defn app []
  [:<>
   [home-component {:isLoggedIn false :user {}}
    [survey {:isLoading false
             :questions @(re-frame/subscribe [::survey-subs/questions])
             :currentQuestionIndex @(re-frame/subscribe [::survey-subs/current-question-index])
             :currentQuestionResponse @(re-frame/subscribe [::survey-subs/current-question-response])
             :showEmailForm @(re-frame/subscribe [::survey-subs/show-email-form])
             :handleAnswerSelection #(re-frame/dispatch [::survey-events/answer-question %])
             :handleNext #(re-frame/dispatch [::survey-events/next-question])
             :handlePrevious #(re-frame/dispatch [::survey-events/previous-question])
             :isEmailFormPending @(re-frame/subscribe [::survey-subs/email-form-pending])
             :email @(re-frame/subscribe [::survey-subs/form :email])
             :onChangeEmail #(re-frame/dispatch [::survey-events/update-email-form :email (-> % .-target .-value)])
             :handleSubmit #(re-frame/dispatch [::survey-events/save-survey])}]]])