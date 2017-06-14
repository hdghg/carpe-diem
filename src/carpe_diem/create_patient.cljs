(ns carpe-diem.create-patient
  (:require [carpe-diem.ui :as ui]
            [reagent.debug :as debug]
            [reagent.core :as r]
            ))

(def form (r/atom {:gender "male"}))
(def date-part (r/atom {}))

(defn left-pad [n val]
  (if (> n (count (str val))) (left-pad n (str 0 val)) (str val))
  )

(defn update-date-part [keyword value]
  (swap! date-part assoc keyword value)
  (let [datestr (str (:year @date-part) "-" (left-pad 2 (:month @date-part)) "-"
                     (left-pad 2 (:day @date-part)))
        date (js/Date. datestr)]
    (if (= "Invalid Date" (.toString date))
      (when (:birth @form) (swap! form assoc :birth nil))
      (when (not= datestr (:birth @form)) (swap! form assoc :birth datestr))
      )
    )
  )

(defn create-patient-screen [{nav :navigation}]
  [ui/scroll
   [ui/text "Name"]
   [ui/input {:on-change-text (partial swap! form assoc :name)}]
   [ui/text "Phone number"]
   ;#(swap! form assoc :phone %)
   [ui/input {:on-change-text (partial swap! form assoc :phone)}]
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
    [ui/input {:placeholder "day" :style {:flex 1} :on-change-text (partial update-date-part :day)}]
    ]
   [ui/text (js/JSON.stringify (clj->js @form))]
   ])

