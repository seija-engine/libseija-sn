package com.seija.sxml.vm

enum Instruction {
    case PushNil
    case PushInt(value:Long)
    case PushChar(value:Char)
    case PushFloat(value:Double)
    case PushString(value:Int)
    case PushKW(value:Int)
    case Push(value:Int)
    case PushUpVar(value:Int)
    case Call(value:Int)

    case ConstructArray(count:Int)
    case ConstructMap(count:Int)
    case ConstructXML(attrCount:Int,childCount:Int)
    case CJump(index:Int)
    case Jump(index:Int)
    case Slide(count:Int)
    case Pop(count:Int)
    case ReplaceTo(idx:Int,count:Int)
    case Return
    case UnWrap

    case NewClosure(index:Int,upvars:Int)
    case CloseClosure(count:Int)

    case AddGlobal(index:Int,lib:String,name:String)
    case LoadGlobal(lib:String,name:String)

    case Add
    case Subtract
    case Multiply
    case Divide
    case LT
    case GT
    case EQ
    case Not

    def adjust():Int = {
        this match
            case PushNil => 1
            case PushInt(value) => 1
            case PushChar(value) => 1
            case PushFloat(value) => 1
            case PushString(value) => 1
            case PushKW(_) => 1
            case LoadGlobal(lib, name) => 1
            case AddGlobal(_,_, _) => 0
            case Push(value) => 1
            case Call(value) => -value
            case CJump(_) => -1
            case NewClosure(_, _) => 1
            case CloseClosure(count) => -1
            case PushUpVar(value) => 1
            case Pop(count) => -count
            case Slide(count) => -count
            case ReplaceTo(_,count) => -count
            case UnWrap => -1
            case ConstructArray(count) => 1 - count
            case Return | Jump(_) | Not => 0
            case ConstructXML(attrCount, childCount) => -(attrCount * 2 + childCount)
            case ConstructMap(count) => 1 - (count * 2)
            case _ => -1
        
    }
}