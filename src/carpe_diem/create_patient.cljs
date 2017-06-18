(ns carpe-diem.create-patient
  (:require [carpe-diem.ui :as ui]
            [reagent.debug :as debug]
            [reagent.core :as r]
            [clojure.string :as str]))

(def form (r/atom {}))
(def date-part (r/atom {}))

(defn- reset-form [] (reset! form {:resourceType "Patient"
                              :name-usual nil
                              :telecom-phone nil
                              :gender "male"
                              :birthDate nil}))

(defn- left-pad [n val]
  (if (> n (count (str val))) (left-pad n (str 0 val)) (str val)))

(defn- update-date-part [keyword value]
  (swap! date-part assoc keyword value)
  (let [datestr (str/join "-" [(:year @date-part)
                               (left-pad 2 (:month @date-part))
                               (left-pad 2 (:day @date-part))])
        date (js/Date. datestr)]
    (if (= "Invalid Date" (.toString date))
      (when (:birthDate @form) (swap! form assoc :birthDate nil))
      (when (not= datestr (:birthDate @form)) (swap! form assoc :birthDate datestr)))))

(defn- submit [success-fn fail-fn]
  (let [name (:name-usual @form) phone (:telecom-phone @form)
        json-body (js/JSON.stringify
               (clj->js
                 (cond-> (select-keys @form [:resourceType :gender :birthDate])
                         (seq name) (assoc :name [{:use "usual" :text name}])
                         (seq phone) (assoc :telecom [{:system "phone" :value nil}]))))]
    (-> (js/fetch "https://carpediem.aidbox.io/fhir/Patient"
                  (clj->js {:method "POST" :headers {"Accept"       "application/json"
                                                     "Content-Type" "application/json"}
                            :body json-body}))
        (.then (fn [resp]
                 (if (.-ok resp)
                   (.json resp)
                   (-> (.text resp) (.then #(throw (str % " status: " (.-status resp))))))))
        (.then (fn [json] (reset-form) (success-fn)))
        (.catch (fn [error] (js/console.error error))))))

(defn create-patient-screen [{nav :navigation}]
  [ui/scroll
   [ui/text "Name"]
   [ui/input {:on-change-text (partial swap! form assoc :name-usual)}]
   [ui/text "Phone number"]
   [ui/input {:on-change-text (partial swap! form assoc :telecom-phone)}]
   [ui/text "Gender"]
   [ui/view {:style {:flex-direction "row" :margin 5}}
    [ui/button {:color    (if (= (:gender @form) "male") "blue" "grey") :title "male"
                :on-press #(swap! form assoc :gender "male")
                }]
    [ui/button {:color    (if (= (:gender @form) "female") "blue" "grey") :title "female"
                :on-press #(swap! form assoc :gender "female")}]]
   [ui/text "Birth date:"]
   [ui/view {:style {:flex-direction "row"}}
    [ui/input {:placeholder "year" :style {:flex 2} :on-change-text (partial update-date-part :year)}]
    [ui/input {:placeholder "month" :style {:flex 1} :on-change-text (partial update-date-part :month)}]
    [ui/input {:placeholder "day" :style {:flex 1} :on-change-text (partial update-date-part :day)}]]
   ;[ui/text (js/JSON.stringify (clj->js @form))]
   [ui/button {:title "Submit" :on-press (fn [] (submit #(.goBack nav) #(ui/alert (js/JSON.stringify %))))}]
   ])

