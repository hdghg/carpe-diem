(ns carpe-diem.core
  (:require [reagent.core :as r :refer [atom]]
            [carpe-diem.patients :as patients]
            [carpe-diem.create-patient :as cp]
            [carpe-diem.patient-info :as pi]
            [reagent.debug :as debug]
            [carpe-diem.login :as login]))

(def ReactNative (js/require "react-native"))

(def AppRegistry (.-AppRegistry ReactNative))
(def ReactNavigation (js/require "react-navigation"))
(def StackNavigator (.-StackNavigator ReactNavigation))

(defn nav-wrapper [component title]
  (let [comp (r/reactify-component component)]
    (aset comp "navigationOptions" #js {"title" title})
    comp))

(def stack-router {"Login" {:screen login/login-screen}
                   "Patients" {:screen (nav-wrapper patients/patients-screen "Patients")}
                   "Create" {:screen (nav-wrapper cp/create-patient-screen "Create")}
                   "Info" {:screen (nav-wrapper pi/patient-info-screen "Patient information")}
                   })

(def sn (r/adapt-react-class (StackNavigator (clj->js stack-router))))

(defn init []
  (patients/refresh)
  (cp/reset-form)
  (.registerComponent AppRegistry "CarpeDiem" #(r/reactify-component sn)))
