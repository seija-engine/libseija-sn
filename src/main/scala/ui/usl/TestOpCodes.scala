package ui.usl

import java.util.ArrayList

object TestOpCodes {
    def test1():CodeFile = {

        var codeFile = CodeFile();
        codeFile.strings.add("CheckBox");
        codeFile.codeList = List(
            OpCode.PushString(0),
            OpCode.PushString(1),
            OpCode.PushBool(false),
        ).toArray;
        codeFile
    }

    def evalCodeList(file:CodeFile) = {
        var index = 0;
        val opCode = file.codeList(index);
        
    } 
}
