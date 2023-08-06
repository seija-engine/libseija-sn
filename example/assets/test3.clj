(if (= 1 1) "Always" "false")

;PushInt(1)
;PushInt(1)
;EQ
;Push(0)
;PushChar(1)
;CharEQ
;CJump(11)
;Push(0)
;PushChar(0)
;CharEQ
;CJump(12)
;PushString(0)
;PushString(1)