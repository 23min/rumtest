(defproject rumtest "0.1.0"
  :description "Testing out rum"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]
  :clean-targets [:target-path]
  :main rumtest.server

  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [org.clojure/clojurescript "0.0-2913" :scope "provided"] ;2816
    [org.clojure/core.async "0.1.346.0-17112a-alpha"]
    [org.clojure/tools.logging "0.3.1"]

    [http-kit "2.1.19"]
    [ring/ring-core "1.3.2" :exclusions [org.clojure/tools.reader]]
    [ring/ring-defaults "0.1.4"]
    [ring/ring-devel "1.3.2"]
    [compojure "1.3.2"]
    [com.taoensso/sente "1.4.0-alpha2"]

;;     [cljsjs/react "0.12.2-6"]
    [cljsjs/react-with-addons "0.12.2-7"]
;;     [com.facebook/react "0.12.2.4"]

    [tailrecursion/javelin "3.7.2"]
    [sablono "0.3.4" :exclusions [cljsjs/react]]
    [rum "0.2.5" :exclusions [cljsjs/react]]
  ]

  :plugins [
    [lein-ring "0.9.1"]
    [lein-cljsbuild "1.0.5"]
    [lein-environ "1.0.0"]
    [lein-ancient "0.5.4"]
  ]

  :hooks [leiningen.cljsbuild]
  :cljsbuild {
    :builds [
      { :id "prod"
        :source-paths ["src/cljs"]
        :target-path ["target/%s"]
        :jar true
        :compiler {
          :preamble      ["public/md5.js"]
          :output-to     "resources/public/js/app.min.js"
          :optimizations :advanced
          :pretty-print  false
        }}
  ]}

  :profiles {
    :dev {
      :cljsbuild {
        :builds [
          { :id "dev"
            :source-paths ["src/cljs"]
            :target-path ["target/%s"]
            :compiler {
              :output-to     "resources/public/js/app.js"
              :output-dir    "resources/public/out"
              :optimizations :none
              :source-map    true
            }}]}
      :env {:dev? true}
    }
  }
)
