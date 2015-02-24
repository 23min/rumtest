(ns rumtest.app
  (:require
    [rum :as rum]
    [sablono.core]
    [tailrecursion.javelin :as jav]

    [cljs.core.async :as async :refer (<! >! put! chan)]
    [taoensso.sente  :as sente :refer (cb-success?)]
  )
  (:require-macros
    [tailrecursion.javelin :as jav :refer [cell= defc= defc]]
    [cljs.core.async.macros :as asyncm :refer (go go-loop)]
  ))

; debugging

(enable-console-print!)

; websocket messaging

(def default-chsk-url-fn
  "`window-location` keys:
    :href     ; \"http://localhost:8080/\"
    :protocol ; \"http:\" ; Note the :
    :hostname ; \"localhost\"
    :host     ; \"localhost:8080\"
    :pathname ; \"\"
    :search   ; \"\"
    :hash     ; \""
  (fn [path {:as window-location :keys [protocol host pathname]} websocket?]
    (str (if-not websocket? protocol (if (= protocol "https:") "wss:" "ws:"))
         "//" host (or path pathname))))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {}) ; Note the same path as before
;;        {:type :auto  ; e/o #{:auto :ajax :ws}
;;         :chsk-url-fn default-chsk-url-fn
;;        })]
      ]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))   ; Watchable, read-only atom


(defn event-loop
  "Handle inbound events."
  []
  (go (loop [[op arg] (:event (<! ch-chsk))]
      (cond
        (= :home/cell (first arg))
          (let [data (nth arg 1)]
            (println data)
            (cond
               (contains? data :cell1/color)
;;              (println (str (:cell1/color data)))))
                                              (reset! colorstyle (:cell1/color data))))
        :else (println (str "Received op: " op " arg: " arg)))
    (recur (:event (<! ch-chsk))))))

; helpers

(declare current-time)
(defn now [] (.getSeconds (js/Date.)))
(defn tick [] (js/setInterval #(reset! current-time (now)) 1000))

; input cells

(jav/defc current-time 0)

; UI driver cells
(def colorstyle (atom "Red"))
(jav/defc color "Red")
;; (jav/defc= colorstyle @color)
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
      [:span {:style {:color (rum/react colorstyle)}}
        (rum/react seconds)]]])

(rum/defc labelTime
  "Simplest component, outputs a label"
  []
  [:label "Tomato"])

(defn init []
  (tick)
  (let [elem (.getElementById js/document "container")
          comp (rum/mount (colored-clock) elem)]))

(event-loop)
