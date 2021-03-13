(defproject demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [cambium/cambium.codec-simple "1.0.0"]
                 [cambium/cambium.core         "1.0.0"]
                 [cambium/cambium.logback.core "0.4.4"]
                 [com.rpl/specter "1.1.3"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [prismatic/schema "1.1.12"]
                 [tupelo "20.12.01"]
                 ]
  :plugins [
    [com.jakemccrary/lein-test-refresh "0.24.1"]
    [lein-ancient "0.6.15"]
    ]

  :global-vars {*warn-on-reflection* false}
  :main ^:skip-aot demo.core

  :source-paths            ["src/clj"]
  :java-source-paths       ["src/java"]
  :test-paths              ["test/clj"]
  :target-path             "target/%s"
  :compile-path            "%s/class-files"
  :clean-targets           [:target-path]

  :profiles {:dev     {:dependencies [ ]}
             :uberjar {:aot :all}}

  :jvm-opts ["-Xms500m" "-Xmx2g" ]
  )
