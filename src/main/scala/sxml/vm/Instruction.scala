package sxml.vm

enum Instruction {
    case PushNil
    case PushInt(value:Long)
    case PushChar(value:Char)
    case PushFloat(value:Double)
    case PushString(value:Long)
    case PushKW(value:Int)
    case Push(value:Long)
    case PushUpVar(value:Int)
    case Call(value:Int)

    case ConstructArray(count:Int)
    case ConstructMap(count:Int)
    case ConstructXML(attrCount:Int,childCount:Int)
    case CJump(index:Int)
    case Jump(index:Int)
    case Slide(count:Int)
    case Pop(count:Int)
    case ReplaceTo(idx:Int)
    case Return
    case UnWrap

    case NewClosure(index:Int,upvars:Int)
    case CloseClosure(count:Int)

    case Add
    case Subtract
    case Multiply
    case Divide
    case LT
    case GT
    case EQ
    case CharEQ
    case StringEQ

    def adjust():Int = {
        this match
            case PushNil => 1
            case PushInt(value) => 1
            case PushChar(value) => 1
            case PushFloat(value) => 1
            case PushString(value) => 1
            case Push(value) => 1
            case Call(value) => -value
            case CJump(_) => -1
            case NewClosure(index, upvars) => 1
            case CloseClosure(count) => -1
            case PushUpVar(value) => 1
            case Pop(count) => -count
            case Slide(count) => -count
            case ReplaceTo(_) => -1
            case UnWrap => -1
            case ConstructArray(count) => 1 - count
            case Return | Jump(_) => 0
            case ConstructXML(attrCount, childCount) => -(attrCount * 2 + childCount)
            case _ => -1
        
    }
}