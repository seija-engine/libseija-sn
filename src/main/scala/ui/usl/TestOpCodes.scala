package ui.usl

import java.util.ArrayList
import ui.usl.CodeBlock


object TestOpCodes {
    def test1():CodeBlock = {
        /*
        <Image width="50" height="50" sprite="default.Btn3On" />
        */
        var codeblock = CodeBlock(Array(
            OpCode.PushString("width"),
            OpCode.PushFloat(50),
            OpCode.PushInt(2),
            OpCode.MakeObject("SizeValue",-1),
            OpCode.PushString("height"),
            OpCode.PushFloat(50),
            OpCode.PushInt(2),
            OpCode.MakeObject("SizeValue",-1),
            //OpCode.PushString("default"),
            //OpCode.PushString("Btn3On"),
            //OpCode.MakeObject("AtlasSprite",-1),
            // OpCode.MakeObject("Image",3),
        ));
        
        ExecContext(codeblock).run();
        codeblock
    }

    def evalCodeList(file:CodeBlock) = {
        var index = 0;
        
    } 
}
