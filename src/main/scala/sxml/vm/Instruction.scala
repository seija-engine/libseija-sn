package sxml.vm

enum Instruction {
    case PushInt(value:Long)
    case PushByte(value:Byte)
    case PushFloat(value:Float)
    case PushString(value:Long)
    case Push(value:Long)
    case Call(value:Long)
    
    case AddInt
    case SubtractInt
    case MultiplyInt
    case DivideInt
    case IntLT
    case IntEQ
}