[1 2 3]
;common
[]
(+ 1 1)
((r 1) "A")
(if isTrue? 1 2)
(if isTrue? 1)
(def pi 3.14)
(fn [n] (* 2 n) 123)
(defn +1 [n] 
  (+ 1 n)
)
(let [a 123 b 456] 1)
(loop [n 10]
  (recur (- n 1))
)
{:a 1 :b 2}
<Item Index=123 />
<List>
  <DataItem Index=1 />
  <DataItem Index=2 />
  <DataItem Index=3 />
</List>

#(+ % 1) ;=>(fn [%] (+ % 1))

#((+ 1 %) (fn [%2 %] (+ % %2)))