(ns carpe-diem.patient-info
  (:require [carpe-diem.ui :as ui]))

(defn patient-info-screen [{nav :navigation}]
  (let [state (.-state nav)
        {:keys [name-usual birthDate gender telecom-phone]} (.-params state)]
    [ui/view {:style {}}
     ;[ui/text (js/JSON.stringify (clj->js params))]
     [ui/text {:style {:font-size 20 :font-weight "bold"}} name-usual]
     [ui/text (str "birth: " birthDate)]
     [ui/text (str "gender: " gender)]
     [ui/text (str "phone: " telecom-phone)]]))
