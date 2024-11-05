(defproject demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [
                 [org.clojure/clojure "1.12.0"]
                 [prismatic/schema "1.4.1"]
                 [tupelo "24.09.30"]
                 ]
  :plugins [
            [com.jakemccrary/lein-test-refresh "0.25.0"]
            [lein-ancient "0.7.0"]
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
