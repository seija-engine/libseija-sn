(defn testloop [n m]
  (if (>= n 0)
    (recur (- n 1) (+ m n))
    m
  )
)

(loop [aa 999]
  (let [n (loop [y 0] (if (>= y 0) (recur (- y 1)) 999))]
    n
  )
)

(testloop 1000000 0)