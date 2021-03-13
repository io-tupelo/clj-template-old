(ns demo.core
  (:use tupelo.core)
  (:require
    [clojure.data.csv :as csv]
    [cambium.core :as log]
    [schema.core :as s])
  (:gen-class))

(def Date s/Str)
(def AdjClosePrice s/Num)

(s/defn read-yahoo-finance-csv :- {Date AdjClosePrice}
  "Returns a map of the adjusted close price where the key is the date
  See tst.demo.core for an example of how this function works"
  [path :- s/Str]
  (->> path
       slurp
       csv/read-csv
       rest
       (mapv (juxt first
                   #(Double. (nth % 5))))
       (into (sorted-map))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (log/info "-main - enter")

  (println "Hello, World! Again!")

  (log/info "-main - leave"))

