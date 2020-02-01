(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [tupelo.string :as ts] )
  (:import [demo Calc]))

(dotest
  (is= 5 (+ 2 3))
  (isnt= 9 (+ 2 3))
  (throws? (/ 1 0)) ; verify that an illegal operation throws an exception

  (is= 3 (add2 1 2))
  (throws? (add2 1 "two")) ; Prismatic Schema will throw since "two" is not a number
  )

(dotest   ; Java source code testing
  (isnt= 5 (Calc/add2 4 1)) ; returns a double, so this fails
  (is= 5.0 (Calc/add2 4 1)) ; correct version
  (is (rel= 5.000000001
        (Calc/add2 4 1) :digits 5)) ; another option
  )

(dotest
  (let [result-0 (with-out-str
                   (-main))
        result-1 (with-system-out-str
                   (-main))
        result-2 (with-system-err-str
                   (-main))]

    (println :result-0) (println result-0)
    (println :result-1) (println result-1)
    (println :result-2) (println result-2)

    (ts/contains-str? result-0 "Hello") ))

(defn tagged-output
  [tag out-fn]
  (let [result-0 (with-out-str
                   (out-fn))
        result-1 (with-system-out-str
                   (out-fn))
        result-2 (with-system-err-str
                   (out-fn))]
    (newline)
    (println tag :result-0) (println result-0)
    (println tag :result-1) (println result-1)
    (println tag :result-2) (println result-2)))

(dotest
  (tagged-output :clj print-clj)
  (tagged-output :out print-out)
  (tagged-output :err print-err)
  )
