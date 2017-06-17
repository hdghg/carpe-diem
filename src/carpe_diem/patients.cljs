(ns carpe-diem.patients
  (:require [reagent.core :as r :refer [atom]]
            [reagent.debug :as debug]
            [carpe-diem.create-patient :as cp]
            [carpe-diem.ui :as ui]))

(def list-view-ds (js/React.ListView.DataSource.
                    (cljs.core/clj->js {:rowHasChanged #(not= %1 %2)})))


(def patients (r/atom []))

(defn render-patient [{:keys [name id birthDate] :as person}]
  [ui/touchable
   [ui/view {:style {}}
    [ui/text {:style {:font-size 20 :font-weight "bold"}} name]
    [ui/text birthDate]]])

(defn entry-mapper [entry-element]
  (let [resource (:resource entry-element)]
    {:id (:id resource) :name (get-in resource [:name 0 :text]) :birthDate (:birthDate resource)}))

(defn fill-patients [json]
  (let [data (js->clj json :keywordize-keys true)
        entries (:entry data)]
    ;(ui/alert (js/JSON.stringify (clj->js data)))
    (reset! patients (map entry-mapper entries))))

(defn refresh []
  ;; TODO: Remove this call
  (debug/warn "Refresh called")
  (-> (js/fetch "https://carpediem.aidbox.io/fhir/Patient"
                (clj->js {:method "GET" :headers {"Accept"       "application/json"
                                                  "Content-Type" "application/json"}}))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.json resp)
                 (-> (.text resp) (.then #(throw (str % " status: " (.-status resp))))))))
      (.then fill-patients)
      (.catch (fn [error] (js/console.error error)))))
(refresh)

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
                           :render-row (comp r/as-element render-patient #(js->clj % :keywordize-keys true))}]
                         [ui/text "no data yet"])]))


