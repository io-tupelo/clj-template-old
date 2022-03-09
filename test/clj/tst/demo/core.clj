(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [tupelo.string :as str]
    ))

(dotest
  (is= 5 (add2 2 3))
  )


