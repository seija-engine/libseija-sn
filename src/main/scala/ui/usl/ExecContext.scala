package ui.usl
import java.util.ArrayList;

enum Value {
    case IntValue(value:Int);
    case FloatValue(value:Float);
    case StringValue(value:String);
    case ByteValue(value:Byte);
    case ObjectValue(value:Any);
    case BoolValue(value:Boolean)

    def castString():Option[String] = {
        this match {
            case Value.StringValue(value) => Some(value);
            case _ => None;
        }
    }
}

class ExecContext(val codeBlock:CodeBlock) {
    var curIndex:Int = 0;
    var stack:ArrayList[Value] = ArrayList();

    def run() = {
        while (curIndex < codeBlock.codeList.size) {
            this.step();
        }
    }

    def step():Unit = {
        val opCode = this.codeBlock.codeList(this.curIndex);
        this.curIndex += 1;
        opCode match {
            case OpCode.PushInt(value) => stack.add(Value.IntValue(value));
            case OpCode.PushFloat(value) => stack.add(Value.FloatValue(value));
            case OpCode.PushString(value) => stack.add(Value.StringValue(value));
            case OpCode.PushByte(value) => stack.add(Value.ByteValue(value));
            case OpCode.PushBool(value) => stack.add(Value.BoolValue(value));
            case OpCode.MakeObject(typeName,fieldCount) => this.makeObject(typeName,fieldCount);
        }
    }

    def makeObject(typeName:String,fieldCount:Int):Option[Any] = {
        if(fieldCount < 0) {
            return None;
        }
        val typInfo = UDSL.getType(typeName);
        if(typInfo.isEmpty) {
            println("Type not found: " + typeName)
            return None;
        }
        //"W" 50 "H" 50
        var startIndex:Int = this.stack.size - fieldCount * 2;
        for(index <- startIndex until(this.stack.size,2)) {
            val value = this.stack.get(index).castString();
            if(value.isEmpty) { println("Field name must be string"); return None; }
            val fieldName = value.get;
            
            
        }
        None
    }
}