"1231232"
"666"
;注释注释hihi
0xffffff
123
3.14
1e2
2.1e-2
-123
-3.14
-1e2
-2.1e-3
\a
\newline
[1 \2 "3" [\a 0.1 0.2]]
(1 2.0 \3)
()
(1 [2 ("H" "E")] (\3))
{"W" 123 "H" 456 "child" [] }
:abc
::fff
{:width 100 :height 20 }
varData
core/RNM
(defn fib [n] (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))

;;;;;XML
<XMLValue K=123 V=456 D="Str" WTF=[1 2 3] FK={:k 1 :v 2} f=(fn [a b] (+ a b)) f2=fib />
<List>
"123123 344"
</List>
<BookList Owner="X">
  <Book Name="BA" />
  <Book Name="BB" />
  <Book Name="BC" />
  (fn [n] (* 2 n))
</BookList>

(if (< index 10)
   <ItemA Index=idx />
   <ItemB Index=idx />
)
(fn gen-fuck-list [lstName]
    <DynList Name=lstName >
        @(map [1 2 3] (fn [idx] <Item index=idx />))
    </DynList>
)

;;;More
(style :key "SliderThumb" :for-type "Thumb"
    {
        :width 26
        :height 26
        :template <ControlTemplate>
                     <Image Name="BG" sprite="default.scale-slider" />
                     <ControlTemplate.VSM>
                        (match [(% :common) (% :checked)]
                            ["Hover","Checked"]     #((set! "BG" :sprite "default.scale-slider-hover"))
                            ["Hover","Unchecked"]   #((set! "BG" :sprite "default.scale-slider-normal"))
                            ["Normal","Checked"]    #((set!   "BG" :sprite "default.scale-slider-normal"))
                            ["Normal","Unchecked"]  #((set! "BG" :sprite "default.scale-slider-normal"))
                        )
                     </ControlTemplate.VSM>
                  </ControlTemplate>
    }
)