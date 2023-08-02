package sxml.compiler
import scala.util.Try
import sxml.vm.{VMExpr,Symbol,AltPattern}
import sxml.parser.{CExpr,LitValue}
import sxml.parser.ParseModule
import sxml.parser.TextSpan
import scala.collection.mutable.ArrayBuffer
import sxml.parser.SpanPos
import sxml.vm.Alternative


class Translator {
    def translateModule(parseModule:ParseModule):Unit = {
        for(cExpr <- parseModule.exprList) {
           val vmExpr = this.translate(cExpr).get
           println(s"VMExpr:${vmExpr}")
        }
    }

    def translate(cExpr:TextSpan[CExpr]):Try[Option[TextSpan[VMExpr]]] = Try {
      val vmExpr:Option[TextSpan[VMExpr]] = cExpr.value match
            case CExpr.Nil => Some(TextSpan(cExpr.pos,VMExpr.VMNil))
            case CExpr.SLit(value) => Some(TextSpan(cExpr.pos,VMExpr.VMLit(value)))
            case CExpr.SVector(lst) => Some(translateVector(cExpr.pos,lst).get)
            case CExpr.SList(lst) => Some(translateList(cExpr.pos,lst).get)
            case CExpr.SSymbol(ns, value) => Some(TextSpan(cExpr.pos,VMExpr.VMSymbol(Symbol(ns,value))))
            case CExpr.SMap(lst) => None
            case CExpr.SXMLElement(tag, attrList, child) => None
            case CExpr.SKeyworld(value, isLocal) => None
            case CExpr.SXMLExpr(value) => None
            case CExpr.SUnWrap(value) => None
            case CExpr.SDispatch(value)  => None
      vmExpr
    }

    protected def translateVector(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
        var vecList:Vector[TextSpan[VMExpr]] = Vector.empty
        for(cExpr <- lst) {
          this.translate(cExpr).get.foreach {v =>
            vecList = vecList.appended(v)
          }
        }
        TextSpan(pos,VMExpr.VMArray(vecList))
    }

    protected def translateList(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      if(lst.isEmpty) throw InvalidList(pos)
      lst.head.value match
        case CExpr.SSymbol(ns, value) => {
          val buildIn = this.tryBuildInFunc(value,pos,lst).get
          if(buildIn.isDefined) buildIn.get else {  translateInvoke(pos,lst).get }
        }
        case CExpr.SList(_) => {  translateInvoke(pos,lst).get }
        case _ => throw InvalidList(pos)
    }

    protected def tryBuildInFunc(fstName:String, pos:SpanPos, lst:ArrayBuffer[TextSpan[CExpr]]):Try[Option[TextSpan[VMExpr]]] = Try {
      fstName match
        case "if" => Some(translateIf(pos,lst).get)
        case "def" => Some(translateDef(pos,lst).get)
        case "fn" => Some(translateFn(pos,lst).get)
        case "match" => None
        case _ => None
    }

    protected def translateInvoke(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val fnExpr = translate(lst.head).get.get
      var argsExpr:Vector[TextSpan[VMExpr]] = Vector.empty
      for(idx <- 1.until(lst.length)) {
        translate(lst(idx)).get.foreach {v =>
          argsExpr = argsExpr.appended(v)
        }
      }
      TextSpan(pos,VMExpr.VMCall(fnExpr,argsExpr))
    }

    protected def translateIf(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      if(lst.length < 3 || lst.length > 4) throw InvalidIf(pos)
      val condExpr = translate(lst(1)).get.getOrElse(throw InvalidIf(pos))
      val trueExpr = translate(lst(2)).get.getOrElse(throw InvalidIf(pos))
      var alts:Vector[Alternative] = Vector(Alternative(AltPattern.Literal(LitValue.LBool(true)),trueExpr))
      if(lst.length > 3) {
        val falseExpr = translate(lst(3)).get.getOrElse(throw InvalidIf(pos))
        alts = alts.appended(Alternative(AltPattern.Literal(LitValue.LBool(false)),falseExpr))
      } else {
        alts = alts.appended(Alternative(AltPattern.Literal(LitValue.LBool(false)),TextSpan(pos,VMExpr.VMNil)))
      }
      TextSpan(pos,VMExpr.VMMatch(condExpr,alts))
    }

    protected def translateDef(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val defExpr = translate(lst(1)).get.getOrElse(throw InvalidDef(pos))
      defExpr.value match
        case VMExpr.VMSymbol(symbol) => {
          val valueExpr = translate(lst(2)).get.getOrElse(throw InvalidDef(pos))
          TextSpan(pos,VMExpr.VMDef(symbol,valueExpr))
        }
        case _ => throw InvalidDef(pos)
    }
    
    protected def translateFn(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      //(fn [a b] ...)
      val argSyms:Vector[Symbol] = lst(1).value match
        case CExpr.SVector(args) => {
          var argSyms:Vector[Symbol] = Vector.empty
          val argsList = args.map(v => translate(v).get.getOrElse(throw InvalidList(pos)))
          for(arg <- argsList) {
            arg.value match
              case VMExpr.VMSymbol(value) => argSyms = argSyms.appended(value)
              case _ => throw InvalidList(pos)
          }
          argSyms
        }
        case _ => throw InvalidList(pos)

        var bodyList:Vector[TextSpan[VMExpr]] = Vector.empty
        for(idx <- 2.until(lst.length)) {
          val expr = translate(lst(idx)).get.getOrElse(throw InvalidFN(pos))
          bodyList = bodyList.appended(expr)
        }
        TextSpan(pos,VMExpr.VMFunc(argSyms,bodyList))
    }
}