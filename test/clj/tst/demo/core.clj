(ns tst.demo.core
  (:use tupelo.core tupelo.test)
  (:require
    [com.rpl.specter :as sp]
    [schema.core :as s]
    [tupelo.misc :as misc]
    [tupelo.schema :as tsk]))

(dotest-focus
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
  (let [ticker-dayrecs {:aaa [{:idx 2 :price 12}
                              {:idx 3 :price 13}
                              {:idx 4 :price 14}]
                        :bbb [{:idx 1 :price 21}
                              {:idx 2 :price 22}
                              {:idx 3 :price 23}
                              {:idx 4 :price 24}]}]
    (is= [2 3 4 1] ; select all the :idx keys
      (it-> ticker-dayrecs
        (sp/select [sp/MAP-VALS sp/ALL] it)
        (sp/transform [sp/ALL] :idx it)
        (distinct it)
        (vec it)))

    ; delete maps where the (<= :idx 2)
    (is= (it-> ticker-dayrecs
           (sp/setval [sp/MAP-VALS sp/ALL #(<= (grab :idx %) 2)] sp/NONE it))
      {:aaa [{:idx 3 :price 13}
             {:idx 4 :price 14}]
       :bbb [{:idx 3 :price 23}
             {:idx 4 :price 24}]}))

  ; delete MapEntries with an odd value
  (is= (it-> {:a 1 :b 2 :c 3 :d 4 :e 5 :f 6}
         (sp/setval [sp/MAP-VALS odd?] sp/NONE it))
    {:b 2, :d 4, :f 6})

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
      {"aaa" {0 {:dnum 0, :iday 0, :price 10},
              1 {:dnum 1, :iday 1, :price 11},
              3 {:dnum 3, :iday 2, :price 13}},

       "bbb" {0 {:dnum 0, :iday 0, :price 20, :price-str "$20.000"},
              1 {:dnum 1, :iday 1, :price 21, :price-str "$21.000"},
              2 {:dnum 2, :iday 2, :price 22, :price-str "$22.000"}}

       "ab"  {0 {:dnum 0, :iday 0, :price 30, :price-str "$30.000"},
              1 {:dnum 1, :iday 1, :price 31, :price-str "$31.000"},
              2 {:dnum 2, :iday 2, :price 32, :price-str "$32.000"}}
       })))


(declare walk-impl walk-map walk-list walk-set)

(defn type-short [arg]
  (cond
    (xmap? arg) :map
    (xsequential? arg) :list
    (set? arg) :set
    :else :leaf))

(dotest
  (is= :map (type-short {:a 1}))
  (is= :list (type-short [1 2 3]))
  (is= :set (type-short #{:a "b" 1}))
  (is= :leaf (type-short "Hello")))

(defn walk-leaf
  [stack leafy]
  (with-spy-indent
    ;(newline)
    ;(spy :walk-leaf leafy)
    ;(spyx-pretty stack)
    ))

(defn walk-map-entry
  [stack me]
  (with-spy-indent
    (newline)
    (spy :walk-map-entry me)
    (spyx-pretty stack)
    (let [me-key        (key me)
          me-val        (val me)
          info-key      {:type :map-entry-key :raw me-key}
          info-val      {:type :map-entry-val :raw me-val}
          stack-new-key (prepend info-key stack)
          stack-new-val (prepend info-val stack)]
      (walk-impl stack-new-key me-key)
      (walk-impl stack-new-val me-val))))

(defn walk-list-entry
  [stack le]
  (with-spy-indent
    (newline)
    (spy :walk-list-entry le)
    (spyx-pretty stack)
    (let [me-key        (xfirst le)
          me-val        (xsecond le)
          info-idx      (glue {:type :list-entry-idx :raw me-key})
          info-val      (glue {:type :list-entry-val :raw me-val})
          stack-new-idx (prepend info-idx stack)
          stack-new-val (prepend info-val stack)]
      (walk-impl stack-new-idx me-key)
      (walk-impl stack-new-val me-val))))


(defn walk-map
  [stack mappy]
  (with-spy-indent
    ;(newline)
    ;(spy :-----------------------------------------------------------------------------)
    ;(spy :walk-map mappy)
    ;(spyx-pretty stack)
    (forv [me mappy]
      (let [me-key    (key me)
            me-val    (val me)
            info      (glue {:type :map-entry :raw me} (vals->map me-key me-val))
            stack-new (prepend info stack)]
        (walk-map-entry stack-new me)))))

(defn walk-list
  [stack listy]
  (with-spy-indent
    ;(newline)
    ;(spy :-----------------------------------------------------------------------------)
    ;(spy :walk-list listy)
    ;(spyx-pretty stack)
    (forv [le (indexed listy)]
      (let [le-idx    (xfirst le)
            le-val    (xsecond le)
            info      (glue {:type :list-entry :raw le} (vals->map le-idx le-val))
            stack-new (prepend info stack)]
        (walk-list-entry stack-new le))))
  )

(defn walk-set
  [stack setty]
  (with-spy-indent
    ;(newline)
    ;(spy :-----------------------------------------------------------------------------)
    ;(spy :walk-set setty)
    ;(spyx-pretty stack)
    (forv [item setty]
      (let [info      {:type :set-elem :raw item}
            stack-new (prepend info stack)]
        (walk-impl stack-new item)))))

(defn walk-impl [stack item]
  (let [type      (type-short item)
        raw       item
        info      (vals->map type raw)
        stack-new (prepend info stack)]
    (newline)
    (spy :walk-impl item)
    (spyx-pretty stack-new)
    (cond
      (= :map type) (walk-map stack-new item)
      (= :list type) (walk-list stack-new item)
      (= :set type) (walk-set stack-new item)
      :else (walk-leaf stack-new item)
      )))

(defn walk [item]
  (walk-impl [] item)
  )

(dotest
  (walk {:a 1 :b [2 3 #{:d :e}]}
    ;{:enter (fn [ctx]
    ;          ()
    ;          (with-map-vals ctx [raw stack]
    ;                    (spyx stack)
    ;                    ))}
    )

  )






