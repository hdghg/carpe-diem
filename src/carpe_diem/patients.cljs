(ns carpe-diem.patients
  (:require [reagent.core :as r]
            [reagent.debug :as debug]
            [carpe-diem.create-patient :as cp]
            [carpe-diem.constants :as cnt]
            [carpe-diem.ui :as ui]
            [carpe-diem.login :as login]))

(def list-view-ds (js/React.ListView.DataSource.
                    (cljs.core/clj->js {:rowHasChanged #(not= %1 %2)})))


(def patients (r/atom []))

(defn render-patient [{:keys [name-usual id birthDate] :as person} nav]
  [ui/touchable {:on-press #(.navigate nav "Info" person)}
   [ui/view {:style {}}
    [ui/text {:style {:font-size 20 :font-weight "bold"}} (or name-usual "UNKNOWN")]
    [ui/text (or birthDate "unknown")]]])

(defn entry-mapper [entry-element]
  (let [resource (:resource entry-element)]
    (-> (select-keys resource [:id :birthDate :gender])
        (assoc :name-usual (get-in resource [:name 0 :text]))
        (assoc :telecom-phone (get-in resource [:telecom 0 :value])))))

(defn fill-patients [json]
  (let [data (js->clj json :keywordize-keys true)
        entries (:entry data)]
    (reset! patients (map entry-mapper entries))))

(defn refresh []
  (if @login/aidbox-token
    (-> (js/fetch (str cnt/patient-endpoint "?access_token=" @login/aidbox-token))
        (.then (fn [resp]
                 (if (.-ok resp)
                   (.json resp)
                   (-> (.text resp) (.then #(throw (str % " status: " (.-status resp))))))))
        (.then fill-patients)
        (.catch (fn [error] (js/console.error error))))))

(defn patients-screen [{nav :navigation :as all}]
  (fn []
    [ui/view
     [ui/view {:style {:flex-direction "row" :justify-content "flex-start"}}
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press #(.navigate nav "Create")
                     }
       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Create"]]
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press (fn [] (reset! patients nil) (refresh))}

       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Refresh"]]
      [ui/touchable {:style    {:background-color "#999" :padding 10 :border-radius 5 :margin-left 5 :margin-top 5}
                     :on-press #(ui/alert "Not implemented yet")}
       [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Log out"]]]
     (if (seq @patients) [ui/list-view
                          {:style      {:margin 10}
                           :dataSource (.cloneWithRows list-view-ds (clj->js @patients))
                           :render-row #(-> (js->clj % :keywordize-keys true)
                                            (render-patient nav)
                                            r/as-element)
                           }]
                         [ui/text "no data yet"])]))


