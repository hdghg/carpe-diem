(ns carpe-diem.ui
  (:require [reagent.core :as r]
            [reagent.debug :as debug]))


(set! js/React (js/require "react-native"))

(def app-registry
  (.-AppRegistry js/React))

;; Basic views

(def view (r/adapt-react-class (.-View js/React)))
(def scroll (r/adapt-react-class (.-ScrollView js/React)))
(def image (r/adapt-react-class (.-Image js/React)))
(def progress-bar (r/adapt-react-class (.-ProgressBarAndroid js/React)))
(def text (r/adapt-react-class (.-Text js/React)))
(def input (r/adapt-react-class (.-TextInput js/React)))
(def touchable (r/adapt-react-class (.-TouchableHighlight js/React)))
(def button (r/adapt-react-class (.-Button   js/React)))

(def linking (.-Linking js/React))

(defn alert [title]
  (js/alert title))
