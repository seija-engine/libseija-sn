(loop [n 0]
  (if (< n 100)
    (recur (+ 1 n))
  )
)

(defn test [n]
  (recur n)  
)