package ui.usl

enum OpCode {
    case MakeObject(typeName:String,fieldCount:Int)
    case PushString(value:String) 
    case PushInt(value:Int)
    case PushFloat(value:Float)
    case PushByte(value:Byte)
    case PushBool(value:Boolean)
}