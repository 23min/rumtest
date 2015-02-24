(ns rumtest.server
  (:require
    [org.httpkit.server     :as server]
    [ring.middleware.reload :as reload]
    [ring.middleware.params :as params]
    [ring.middleware.defaults]
    [ring.util.response     :as response]

    [clojure.tools.logging  :as logging]

    [compojure.core         :as comp :refer (defroutes GET POST)]
    [compojure.route        :as route]

    [clojure.core.async     :as async :refer (<! <!! >! >!! put! chan go go-loop)]
    [taoensso.sente         :as sente]
    [taoensso.sente.server-adapters.http-kit]
  )
  (:gen-class))

; config ======================================================================

(defonce stop-server-fn (atom nil))

(def web-server-adapter taoensso.sente.server-adapters.http-kit/http-kit-adapter)


; logic =======================================================================



; handlers ====================================================================


(defn home-handler [req]
  (response/content-type (response/resource-response "public/index.html") "text/html"))


(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
      (sente/make-channel-socket! web-server-adapter)]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defn start-broadcaster! []
  (go-loop [i 0]
    (<! (async/timeout 10000))
    (println (format "Broadcasting server>user: %s" @connected-uids))
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:home/cell {:cell1/color (rand-nth ["Red" "Blue" "Black" "Yellow" "Green"])}]))
    (recur (inc i))))

(defroutes route-handler
  (GET  "/"        req (home-handler req))
  (GET  "/chsk"    req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk"    req (ring-ajax-post                req))
;;   (POST "/login" req (login! req))

;;    (compojure.core/context "/api"    [] api-handler)
;;    (compojure.core/GET     "/events" [] events-handler)

  (route/resources "/")
  (route/not-found "Page not found"))


; entry points ================================================================

(def my-ring-handler
  (let [ring-defaults-config
        (assoc-in ring.middleware.defaults/site-defaults [:security :anti-forgery]
          {:read-token (fn [req] (-> req :params :csrf-token))})]

    ;; NB: Sente requires the Ring `wrap-params` + `wrap-keyword-params`
    ;; middleware to work. These are included with
    ;; `ring.middleware.defaults/wrap-defaults` - but you'll need to ensure
    ;; that they're included yourself if you're not using `wrap-defaults`.
    ;;
    (ring.middleware.defaults/wrap-defaults route-handler ring-defaults-config)))

(defn -main
  [& {:as opts}]
  (let [ip       (get opts "--ip" "0.0.0.0")
        port (-> (get opts "--port" "8080") (Integer/parseInt))
        dev      (contains? opts "--reload")]

    (let [handler (if dev
                    (reload/wrap-reload #'my-ring-handler {:dirs ["src/clj"]})
                    my-ring-handler)]
      (reset! stop-server-fn (server/run-server handler {:port port :ip ip})))

;;     (sente/start-chsk-router-loop! sente-message-router ch-chsk)
;;     (start-broadcaster!)
    (logging/info "Server ready at" (str ip ":" port))))


(defn start-server []
  (-main))


(defn stop-server []
  (when-not (nil? @stop-server-fn)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@stop-server-fn :timeout 100)
    (reset! stop-server-fn nil)))

;; (start-server)

;; (stop-server)
(start-broadcaster!)
