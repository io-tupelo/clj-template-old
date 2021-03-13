(ns tst.demo.core
  (:use tupelo.test)
  (:require
    [demo.core :as core]
    [tupelo.core :as t]))

(dotest
  (let [spy-prices (core/read-yahoo-finance-csv "resources/SPY_2016-03-04_2021-03-03.csv")]
    (is= (->> spy-prices
           (take 4)
           (into {}))
         {"2016-03-04" 181.977966,
          "2016-03-07" 182.123245,
          "2016-03-08" 180.134842,
          "2016-03-09" 181.024643})
    (is= (->> spy-prices
              (take-last 4)
              (into {}))
         {"2021-02-26" 380.359985
          "2021-03-01" 389.579987
          "2021-03-02" 386.540009
          "2021-03-03" 381.420013})))
