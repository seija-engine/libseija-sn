(def pi 3.1415)
(defn loopFn [n]
  (loop [a n]
    (if (> a 0)
      (recur (- a 1))
      666
    )    
  ) 
)

(defn doFunc []
  (let [nnn 5]
    (loopFn 5)  
  )
)

(doFunc)