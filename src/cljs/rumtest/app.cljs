(ns rumtest.app
  (:require
    [rum]
    [sablono.core]
    [tailrecursion.javelin :as jav]
  )
  (:require-macros
    [tailrecursion.javelin :as jav :refer [cell= defc= defc]])
  )

; debugging

(enable-console-print!)

; helpers

(declare current-time)
(defn now [] (.getSeconds (js/Date.)))
(defn tick [] (js/setInterval #(reset! current-time (now)) 1000))

; input cells

(jav/defc current-time 0)

; UI driver cells

(jav/defc color "Red")
(jav/defc= seconds (str "Seconds: " current-time))

; UI components

(rum/defc colored-clock < rum/reactive
  "Outputs:
   <div class=\"colored-clock\" data-reactid=\".0\">
   <div data-reactid=\".0.0\">
   <span style=\"color:Red;\" data-reactid=\".0.0.0\">Seconds: 0</span></div></div>"
  []
  [:.colored-clock
    [:div
      [:span {:style {:color @color}}
        (rum/react seconds)]]])

(rum/defc labelTime
  "Simplest component, outputs a label"
  []
  [:label "Tomato"])

(defn init []
  (tick)
  (let [elem (.getElementById js/document "container")
          comp (rum/mount (colored-clock) elem)]))
