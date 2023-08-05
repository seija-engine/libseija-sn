package sxml.vm

enum Instruction {
    case PushNil
    case PushInt(value:Long)
    case PushChar(value:Char)
    case PushBool(value:Boolean)
    case PushFloat(value:Double)
    case PushString(value:Long)
    case Push(value:Long)
    case Call(value:Int)

    case ConstructArray(count:Int)

    case Return

    case Add
    case Subtract
    case Multiply
    case Divide
    case LT
    case GT
    case EQ


    def adjust():Int = {
        this match
            case PushNil => 1
            case PushInt(value) => 1
            case PushChar(value) => 1
            case PushBool(value) => 1
            case PushFloat(value) => 1
            case PushString(value) => 1
            case Push(value) => 1
            case Call(value) => -value
            case ConstructArray(count) => 1 - count
            case Return => 0
            case _ => -1
        
    }
}