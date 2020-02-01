(ns demo.core
  (:use tupelo.core)
  (:require
    [clojure.string :as str]
    [schema.core :as s])
  (:gen-class))

(s/defn add2 :- s/Num
  [x :- s/Num
   y :- s/Num]
  (+ x y))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World! Again!"))

