(def a 90)
(loop [n 0]
  (if (> n 0) 
      1000
      (recur 2000)
  )
)