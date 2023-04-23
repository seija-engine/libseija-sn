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

    def castInt():Option[Int] = {
        this match {
            case Value.IntValue(value) => Some(value);
            case _ => None;
        }
    }

    def castFloat():Option[Float] = {
        this match {
            case Value.FloatValue(value) => Some(value);
            case _ => None;
        }
    }

    def unwrap():Any = {
        this match {
            case Value.IntValue(value) => value;
            case Value.FloatValue(value) => value;
            case Value.StringValue(value) => value;
            case Value.ByteValue(value) => value;
            case Value.ObjectValue(value) => value;
            case Value.BoolValue(value) => value;
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
        opCode match {
            case OpCode.PushInt(value) => stack.add(Value.IntValue(value));
            case OpCode.PushFloat(value) => stack.add(Value.FloatValue(value));
            case OpCode.PushString(value) => stack.add(Value.StringValue(value));
            case OpCode.PushByte(value) => stack.add(Value.ByteValue(value));
            case OpCode.PushBool(value) => stack.add(Value.BoolValue(value));
            case OpCode.MakeObject(typeName,fieldCount) => {
                val makeObj = this.makeObject(typeName,fieldCount);
                println(makeObj);
                stack.add(makeObj);
                println(stack)
            }
        }
        this.curIndex += 1;
    }

    def makeObject(typeName:String,fieldCount:Int):Value = {
        val uTyp:Option[UDSLType[_]] = UDSL.getType(typeName);
        if(uTyp.isEmpty) {
           throw new Exception("Type not found");
        }
        typeFormStack(uTyp.get.typInfo)
        /*
        if(fieldCount < 0) {
            uTyp.get.typInfo match {
                case ui.usl.TypeInfo.Class(value) =>
                case TypeInfo.NumberEnum(name) =>
                case ui.usl.TypeInfo.Enum(name,fields) => {
                    val value = enumFormStack(name,fields).unwrap();
                    println(value)
                }
                case _ => return None;
            }
            return None;
        }
        
        var startIndex:Int = this.stack.size - fieldCount * 2;
        println(startIndex)
        for(index <- startIndex until(this.stack.size,2)) {
            println(index)
            val value = this.stack.get(index).castString();
            if(value.isEmpty) { println("Field name must be string"); return None; }
            val fieldName = value.get;
            uTyp.get.typInfo match {
                case ui.usl.TypeInfo.Class(value) =>
                case TypeInfo.NumberEnum(name) =>
                case ui.usl.TypeInfo.Enum(_,fields) =>
                case _ => return None;
            }
        }
        None*/
    }

    def typeFormStack(typInfo:TypeInfo):Value = {
        typInfo match
            case ui.usl.TypeInfo.Class(value) => ???
            case TypeInfo.NumberEnum(name) => ???
            case ui.usl.TypeInfo.Enum(name, fields) => enumFormStack(name,fields)
            case _ => this.stack.remove(this.stack.size - 1)
    }

    def enumFormStack(name:String,fields:List[EnumItem]):Value = {
        val tag = this.stack.remove(this.stack.size - 1).castInt();
        if(tag.isEmpty) { throw new Exception("Enum tag must be int"); }
        val itemType = fields(tag.get);
        val uTyp = UDSL.getType(name).get;
        if(itemType.value.isEmpty) {
           return Value.ObjectValue(uTyp.fromEnum(tag.get,List()));
        }
        val elemType = typeFormStack(itemType.value.get);
        Value.ObjectValue(uTyp.fromEnum(tag.get,List(elemType.unwrap())))
    }
}