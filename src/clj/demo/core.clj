(ns demo.core
  (:use tupelo.core)
  (:require
    [cambium.core :as log]
    [schema.core :as s])
  (:gen-class))

(s/defn add2 :- s/Num
  "An example to demonstrate Plumatic Schema."
  [x :- s/Num
   y :- s/Num]
  (+ x y))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (log/info "-main - enter")

  (println "Hello, World! Again!")

  (log/info "-main - leave"))

