(ns _bootstrap
  "This namespace is used to perform one-time tasks at the beginning of a test run,
   such as printing the Clojure version."
  (:use tupelo.test)
  (:require
    [schema.core :as s]
    [tupelo.core :as t]))

; Prismatic Schema type definitions
(s/set-fn-validation! true) ; enforce fn schemas

(dotest
  (t/print-versions))
