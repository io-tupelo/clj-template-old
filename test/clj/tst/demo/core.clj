(ns tst.demo.core
  (:use tupelo.core tupelo.test)
  (:require
    [com.rpl.specter :as sp]
    [schema.core :as s]
    [tupelo.misc :as misc]
    [tupelo.schema :as tsk]))

(dotest
  ; (is= true false)

  ; inc doubly-nested map values
  (is= (it-> {:a {:aa 1} :b {:ba 2 :bb 3}}
         (sp/transform [sp/MAP-VALS sp/MAP-VALS] inc it))
    {:a {:aa 2} :b {:ba 3 :bb 4}})

  ; inc all map values with key `:a` and odd value
  (is= (it-> [{:a 1} {:a 2} {:a 3} {:a 4}]
         (sp/transform [sp/ALL :a even?] inc it))
    [{:a 1} {:a 3} {:a 3} {:a 5}])

  ; collect all nums divisible by 3
  (let [mul-3? (fn [arg] (zero? (mod arg 3)))]
    (is= (it-> [[1 2 3 4] [] [5 3 2 18] [2 4 6] [12]]
           (sp/select [sp/ALL sp/ALL mul-3?] it))
      [3 3 18 6 12])

    ; delete all nums divisible by 3
    (is= (it-> [[1 2 3 4] [] [5 3 2 18] [2 4 6] [12]]
           (sp/setval [sp/ALL sp/ALL mul-3?] sp/NONE it))
      [[1 2 4] [] [5 2] [2 4] []]))

  ; delete maps where the :idx value is <= 2
  (is= (it-> {:aaa [{:idx 2 :price 12}
                    {:idx 3 :price 13}
                    {:idx 4 :price 14}]
              :bbb [{:idx 1 :price 21}
                    {:idx 2 :price 22}
                    {:idx 3 :price 23}
                    {:idx 4 :price 24}]}
         (sp/setval [sp/MAP-VALS sp/ALL #(<= (grab :idx %) 2)] sp/NONE it))
    {:aaa [{:idx 3 :price 13}
           {:idx 4 :price 14}]
     :bbb [{:idx 3 :price 23}
           {:idx 4 :price 24}]})

  ; Doesn't work unless sorted map
  (let [m1 {:aaa {0 {:dnum 0 :price 10}
                  2 {:dnum 2 :price 12}
                  1 {:dnum 1 :price 11}}
            :bbb {2 {:dnum 2 :price 22}
                  1 {:dnum 1 :price 21}
                  0 {:dnum 0 :price 20}}}]
    (isnt= (it-> m1
             (sp/select [:aaa sp/MAP-VALS :price] it))
      [10 11 12])
    (is= (it-> m1
           (misc/walk-map->sorted it)
           (sp/select [:aaa sp/MAP-VALS :price] it))
      [10 11 12])))

(dotest
  (let [state-0  {"aaa" {0 {:dnum 0 :price 10}
                         3 {:dnum 3 :price 13}
                         1 {:dnum 1 :price 11}}
                  "bbb" {2 {:dnum 2 :price 22}
                         1 {:dnum 1 :price 21}
                         0 {:dnum 0 :price 20}}}

        ; add the "day index" 0,1,2... to each entity map
        add-iday (fn [mkt-info ticker]
                   (let [day-nums   (sp/select [ticker sp/MAP-KEYS] mkt-info)
                         dnum->iday (->sorted-map (zipmap (sort day-nums) (range)))
                         result-1   (->> mkt-info
                                      (sp/transform [ticker sp/MAP-VALS]
                                        (fn [info-map]
                                          (let [dnum (grab :dnum info-map)
                                                iday (grab dnum dnum->iday)]
                                            (glue info-map (vals->map iday))))))]
                     result-1))
        state-1  (add-iday state-0 "aaa")

        ; add an arbitrary field "price-str" each entity map
        add-strs (fn [mkt-info ticker]
                   (->> mkt-info
                     (sp/transform [ticker sp/MAP-VALS]
                       (s/fn [info-map :- tsk/KeyMap]
                         (let [price-str (format "$%.3f" (bigdec (grab :price info-map)))]
                           (glue info-map (vals->map price-str)))))))
        state-2  (it-> state-1
                   (add-iday it "bbb")
                   (add-strs it "bbb"))]
    ; can validate state at any time desired here or in functions
    (is (s/validate {s/Str {s/Int tsk/KeyMap}} state-0))

    (is= state-1
      {"aaa"
       {0 {:dnum 0, :iday 0, :price 10},
        1 {:dnum 1, :iday 1, :price 11},
        3 {:dnum 3, :iday 2, :price 13}},
       "bbb"
       {0 {:dnum 0, :price 20},
        1 {:dnum 1, :price 21},
        2 {:dnum 2, :price 22}}})

    (is= (misc/walk-map->sorted state-2)
      {"aaa"
       {0 {:dnum 0, :iday 0, :price 10},
        1 {:dnum 1, :iday 1, :price 11},
        3 {:dnum 3, :iday 2, :price 13}},
       "bbb"
       {0 {:dnum 0, :iday 0, :price 20, :price-str "$20.000"},
        1 {:dnum 1, :iday 1, :price 21, :price-str "$21.000"},
        2 {:dnum 2, :iday 2, :price 22, :price-str "$22.000"}}})

    ))



