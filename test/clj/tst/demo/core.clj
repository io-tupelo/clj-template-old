(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [tupelo.string :as ts])
  (:import [demo Calc]))

(dotest
  (is= 5 (+ 2 3))
  (isnt= 9 (+ 2 3))
  (throws? (/ 1 0)) ; verify that an illegal operation throws an exception

  (is= 3 (add2 1 2))
  (throws? (add2 1 "two"))) ; Prismatic Schema will throw since "two" is not a number


(dotest   ; Java source code testing
  (isnt= 5 (Calc/add2 4 1)) ; returns a double, so this fails
  (is= 5.0 (Calc/add2 4 1)) ; correct version
  (is (rel= 5.000000001
        (Calc/add2 4 1) :digits 5))) ; another option

