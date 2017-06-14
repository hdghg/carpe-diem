(ns carpe-diem.core
  (:require [reagent.core :as r :refer [atom]]
            [carpe-diem.patients :as patients]
            [carpe-diem.create-patient :as cp]
            [reagent.debug :as debug]
            [carpe-diem.ui :as ui]))

(def ReactNative (js/require "react-native"))

(def AppRegistry (.-AppRegistry ReactNative))
(def ReactNavigation (js/require "react-navigation"))
(def StackNavigator (.-StackNavigator ReactNavigation))

(defn nav-wrapper [component title]
  (let [comp (r/reactify-component component)]
    (aset comp "navigationOptions" #js {"title" title})
    comp))

(def stack-router {
                   "Patients" {:screen (nav-wrapper patients/patients-screen "Patients")}
                   "Create" {:screen (nav-wrapper cp/create-patient-screen "Create")}
                   })

(def sn (r/adapt-react-class (StackNavigator (clj->js stack-router))))

(defn init []
  (.registerComponent AppRegistry "CarpeDiem" #(r/reactify-component sn)))
