package ui.usl

enum OpCode {
    case MakeControl(fieldCount:Int)
    case PushString(index:Int) 
    case PushInt(value:Int)
    case PushByte(value:Byte)
    case PushBool(value:Boolean)
}