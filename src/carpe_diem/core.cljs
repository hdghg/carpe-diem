(ns carpe-diem.core
  (:require [reagent.core :as r]
            [carpe-diem.patients :as patients]
            [carpe-diem.create-patient :as cp]
            [carpe-diem.patient-info :as pi]
            [reagent.debug :as debug]
            [carpe-diem.login :as login]
            [carpe-diem.utils :as utils]))

(def ReactNative (js/require "react-native"))

(def AppRegistry (.-AppRegistry ReactNative))
(def ReactNavigation (js/require "react-navigation"))
(def StackNavigator (.-StackNavigator ReactNavigation))

(defn nav-wrapper [component title]
  (doto (r/reactify-component component)
    (aset "navigationOptions" #js {"title" title})))

(def stack-router {
                   "Login"    {:screen (doto (r/reactify-component login/login-screen)
                                         (aset "navigationOptions" #js {"header" nil}))}
                   "Patients" {:screen (nav-wrapper patients/patients-screen "Patients")}
                   "Create"   {:screen (nav-wrapper cp/create-patient-screen "Create")}
                   "Info"     {:screen (nav-wrapper pi/patient-info-screen "Patient information")}})

(def sn (r/adapt-react-class (StackNavigator (clj->js stack-router))))

(defn init []
  (.registerComponent AppRegistry "CarpeDiem" #(r/reactify-component sn)))
