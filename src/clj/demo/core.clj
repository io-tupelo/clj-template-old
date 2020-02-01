(ns demo.core
  (:use tupelo.core)
  (:require
    [cambium.core :as log]
    [schema.core :as s])
  (:gen-class))

(s/defn add2 :- s/Num
  [x :- s/Num
   y :- s/Num]
  (+ x y))

(defn print-clj
  [] (println "clojure.core/println"))
(defn print-out
  [] (-> System/out (.println "System.out.println")))
(defn print-err
  [] (-> System/err (.println "System.err.println")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (log/info "-main - enter")

  (println "Hello, World! Again!")

  (log/info "-main - leave"))

