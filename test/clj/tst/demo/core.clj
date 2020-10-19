(ns tst.demo.core
  (:use demo.core tupelo.core tupelo.test)
  (:require
    [libpython-clj.python :as py]
    [libpython-clj.python :refer [py. py.. py.-]]
    [libpython-clj.require :refer [require-python]]
    ))

(py/initialize!
  :python-executable "/usr/bin/python3.8"
  :library-path "/usr/lib/python3.8/config-3.8-x86_64-linux-gnu/libpython3.8.so")

(require-python '[numpy :as np])

(dotest
  (spyx-pretty (np/array [[1 2] [3 4]]))
  (let [x (spyx (np/arange 6))
        y (spyx-pretty (np/reshape x [2 3]))
        ]
    )
  (spy-pretty :result
    (-> (np/arange 6) (np/reshape [2 3])))

  )

