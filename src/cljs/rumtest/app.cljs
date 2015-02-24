(ns rumtest.app
  (:require
    [rum]
    [sablono.core]
    [tailrecursion.javelin :as jav]

    [cljs.core.async :as async :refer (<! >! put! chan)]
    [taoensso.sente  :as sente :refer (cb-success?)]
  )
  (:require-macros
    [tailrecursion.javelin :as jav :refer [cell= defc= defc]]
    [cljs.core.async.macros :as asyncm :refer (go go-loop)]
  ))

; initialize ==================================================================

(enable-console-print!)

(declare color)

; websocket messaging =========================================================

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))   ; Watchable, read-only atom

(defn event-loop
  "Handle inbound events and update Javelin cells"
  []
  (go (loop [[op arg] (:event (<! ch-chsk))]
      (cond
        (= :home/cell (first arg))
          (let [data (nth arg 1)]
            (println data)
            (cond
               (contains? data :cell1/color) (reset! color (:cell1/color data))))
        :else (println (str "Received op: " op " arg: " arg)))
    (recur (:event (<! ch-chsk))))))

; helpers =====================================================================

(declare current-time)
(defn now [] (.getSeconds (js/Date.)))
(defn tick [] (js/setInterval #(reset! current-time (now)) 1000))

; input cells =================================================================

(jav/defc current-time 0)

; UI driver cells =============================================================

(jav/defc color "Red")
(jav/defc= seconds (str "Seconds: " current-time))

; UI components ===============================================================

(rum/defc colored-clock < rum/reactive
  "Clock that changes color once every 10s"
  []
  [:.colored-clock
    [:div
      [:span {:style {:color (rum/react color)}}
        (rum/react seconds)]]])

(rum/defc labelFooter
  "Simplest component, outputs a label"
  []
  [:label "Footer"])

(rum/defc page < rum/static []
  [:div
    [:div#header
      [:h1 "Rum Test"]]
    [:div#content
      [:div [:span
              (colored-clock)]]]
    [:div#footer
      [:div [:span (labelFooter)]]]]
     )

(defn render []
    (rum/mount (page) (.-body js/document)))

; init ========================================================================

(defn init []
  (tick)
  (event-loop)
  (render))



