(ns carpe-diem.login
  (:require [reagent.core :as r]
            [carpe-diem.ui :as ui]
            [carpe-diem.constants :as cnt]
            [clojure.string :as str]))

(def aidbox-token (atom nil))

(defn get-aidbox-token [nav auth0-token]
  (-> (js/fetch (str cnt/userinfo-uri auth0-token))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.json resp)
                 (-> (.text resp) (.then #(throw (str % " status: " (.-status resp))))))))
      (.then (fn [json]
               (reset! aidbox-token (.-idp_token json))
               (.navigate nav "Patients")))
      (.catch (fn [error] (js/console.error error))))
  )

(def prev-url (atom nil))

(defn state-changed [nav url]
  (if (and (str/starts-with? url cnt/callback-uri) (not= url @prev-url))
    (let [query-map (apply hash-map (-> (str/replace-first url cnt/callback-uri "")
                                        (str/split #"[&=]")))
          auth0-token (get query-map "access_token")]
      (reset! prev-url url)
      (get-aidbox-token nav auth0-token))))

(def login-screen
  (r/reactify-component
    (fn [{nav :navigation :as all}]
      [ui/web-view {:source #js {"uri" cnt/auth-uri}
                    :on-navigation-state-change #(state-changed nav (.-url %))}])))
