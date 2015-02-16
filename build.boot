(set-env!
 :source-paths    #{"src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "0.0-2814-0" :scope "test"]
                 [adzerk/boot-cljs-repl     "0.1.8"      :scope "test"]
                 [adzerk/boot-reload        "0.2.4"      :scope "test"]
                 [pandeiro/boot-http        "0.6.2"      :scope "test"]
                 [org.clojure/clojurescript "0.0-2850"]
                 [cljsjs/boot-cljsjs        "0.4.6"]
                 [cljsjs/react              "0.12.2-5"]
                 [weasel                    "0.6.0-SNAPSHOT"]
                 [com.cemerick/piggieback   "0.1.5"]

                 ; app
                 [tailrecursion/javelin     "3.7.2"]
                 [rum                       "0.2.4"]
                 ])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[cljsjs.boot-cljsjs    :refer [from-cljsjs]]
 )

(deftask build []
  (comp (speak)
        (from-cljsjs)
        (cljs)
        ))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)
        ))

(deftask production []
  (task-options! cljs {:optimizations :advanced
                       ;; pseudo-names true is currently required
                       ;; https://github.com/martinklepsch/pseudo-names-error
                       ;; hopefully fixed soon
                       :pseudo-names true}
                 from-cljsjs {:profile :production})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :unified-mode true
                       :source-map true}
                 reload {:on-jsload 'rumtest.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))
