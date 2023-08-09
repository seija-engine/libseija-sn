(loop [n 100]
  (match (> n 0)
      true  1
      false (recur (+ n 1))
    )
)

(defn fff [a]
  (if (< a 1)
    (loop [b a]
       (recur (+ b 1))
    )
    (recur (+ a 1))
  )
)
;100                 PushInt(100)
;100 100             Push(0)
;100 100 0           PushInt(0)
;100 true            GT
;100 true true       Push(1)
;100 true true true  PushChar(1)
;100 true true       CharEQ
;100 true            CJump(12)
;                    Pop(2)
