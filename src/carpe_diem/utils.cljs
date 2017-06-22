(ns carpe-diem.utils
  (:require [reagent.core :as r]))

(def CookieManager (js/require "react-native-cookies"))

(defn clear-cookies [] (.clearAll CookieManager (fn [& _])))

(def ReactNavigation (js/require "react-navigation"))

(def NavigationActions (.-NavigationActions ReactNavigation))

(def patients (.navigate NavigationActions #js {"routeName" "Patients"}))

(def reset (.reset NavigationActions
                   (clj->js {:index 0
                             :actions [patients]})))
