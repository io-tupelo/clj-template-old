(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [tupelo.string :as str]
    ))

(verify
  (is= 5 (add2 2 3))
  )


