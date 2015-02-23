(ns rumtest.server
  (:require
    [org.httpkit.server :as server]
    [ring.middleware.reload :as reload]
    [ring.middleware.params :as params]
    [ring.util.response :as response]
    [clojure.tools.logging :as logging]
    [compojure core route]
  )
  (:gen-class))


(defonce server (atom nil))


(defn home-handler [req]
  (response/content-type (response/resource-response "public/index.html") "text/html"))


(def route-handler
  (->
    (compojure.core/routes
;;    (compojure.core/context "/api"    [] api-handler)
;;    (compojure.core/GET     "/events" [] events-handler)
      (compojure.core/GET     "/"       [] home-handler))
      (compojure.route/resources "/")
      (compojure.route/not-found "Page not found")))


(defn -main [& {:as opts}]
  (let [ip       (get opts "--ip" "0.0.0.0")
        port (-> (get opts "--port" "8080") (Integer/parseInt))
        dev      (contains? opts "--reload")]

    (let [handler (if dev
                    (reload/wrap-reload #'route-handler {:dirs ["src/clj"]})
                    route-handler)]
      (reset! server (server/run-server handler {:port port :ip ip})))
    (logging/info "Server ready at" (str ip ":" port))))


(defn start-server []
  (-main))


(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

;; (start-server)

;; (stop-server)
