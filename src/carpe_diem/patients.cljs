(ns carpe-diem.patients
  (:require [reagent.core :as r :refer [atom]]
            [carpe-diem.create-patient :as cp]
            [carpe-diem.ui :as ui]))


(defn patients-screen [{nav :navigation :as all}]
  (fn []
    [ui/view
     [ui/view {:style {:flex-direction "row" :justify-content "flex-start"}}
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press #(.navigate nav "Create")
                     }
       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Create"]]
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press #(ui/alert "Not implemented yet")}
       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Refresh"]]
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press #(ui/alert "Not implemented yet")}
       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Log out"]]]

     [ui/view {:style {:margin 10}}
      [ui/text {:style {:font-size 30 :font-weight "100"}} "No data yet1"]
      [ui/text {:style {:font-size 30 :font-weight "100"}} "No data yet2"]
      [ui/text {:style {:font-size 30 :font-weight "100"}} "No data yet3"]
      ]]))

