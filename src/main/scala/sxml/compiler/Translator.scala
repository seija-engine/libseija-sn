package sxml.compiler
import scala.util.Try
import sxml.vm.{VMExpr,Symbol,AltPattern,vmExprCastTo}
import sxml.parser.{CExpr,LitValue}
import sxml.parser.ParseModule
import sxml.parser.TextSpan
import scala.collection.mutable.ArrayBuffer
import sxml.parser.SpanPos
import sxml.vm.Alternative
import scala.collection.immutable.Vector
import sxml.vm.ImportInfo

case class TranslatorModule(
  val exportSymbols:ArrayBuffer[String],
  val imports:ArrayBuffer[ImportInfo],
  val exprList:Vector[TextSpan[VMExpr]]
)

class Translator {
    val exportSymbols:ArrayBuffer[String] = ArrayBuffer.empty
    val importInfos:ArrayBuffer[ImportInfo] = ArrayBuffer.empty

    def translateModule(parseModule:ParseModule):Try[TranslatorModule] = Try {
      this.exportSymbols.clear()
      this.importInfos.clear();
      val lst = parseModule.exprList.map(translate(_).get)
      TranslatorModule(
        this.exportSymbols.clone(),
        this.importInfos.clone(),
        lst.toVector
      )
    }
    
    def translate(cExpr:TextSpan[CExpr]):Try[TextSpan[VMExpr]] = Try {
      val vmExpr:TextSpan[VMExpr] = cExpr.value match
            case CExpr.Nil => TextSpan(cExpr.pos,VMExpr.VMNil)
            case CExpr.SLit(value) => TextSpan(cExpr.pos,VMExpr.VMLit(value))
            case CExpr.SVector(lst) => translateVector(cExpr.pos,lst).get
            case CExpr.SList(lst) => translateList(cExpr.pos,lst).get
            case CExpr.SSymbol(ns, value) =>TextSpan(cExpr.pos,VMExpr.VMSymbol(Symbol(ns,value)))
            case CExpr.SMap(lst) => translateMap(cExpr.pos,lst).get
            case CExpr.SKeyworld(value, isLocal) => TextSpan(cExpr.pos,VMExpr.VMKeyword(value,isLocal))
            case CExpr.SXMLElement(tag, attrList, child) => translateXMLElement(cExpr.pos,tag,attrList,child).get
            case CExpr.SUnWrap(value) => TextSpan(cExpr.pos,VMExpr.VMUnWrap(translate(value).get))
            case CExpr.SDispatch(value)  => translateDispatch(cExpr.pos,value).get
      vmExpr
    }

    protected def translateDispatch(pos:SpanPos,cExpr:TextSpan[CExpr]):Try[TextSpan[VMExpr]] = Try {
      cExpr.value match
        case CExpr.SList(lst) => {
          val maxArgCount = this.searchMaxSymbol(0,cExpr.value)
          val argSyms = 1.to(maxArgCount).map(idx => {
            if(idx == 1) Symbol(None,"%") else Symbol(None,s"%${idx}")
          }).toVector
          val bodyList = this.takeBodyList(pos,0,lst).get
          TextSpan(pos,VMExpr.VMFunc(argSyms,bodyList))
        }
        case _ => throw InvalidDispatch(pos)
    }

    protected def searchMaxSymbol(lastCount:Int,expr:CExpr):Int = {
      expr match
        case CExpr.SSymbol(ns, value) => {
          if(value.head == '%') {
            if(value == "%") 1 else {
              val tailString = value.tail
              val count = tailString.toIntOption.getOrElse(0)
              if(count > lastCount) count else lastCount
            }
          } else { 0 }
        }
        case CExpr.SList(lst) => {
          if(lst.length > 0 && lst.head.value.isInstanceOf[CExpr.SSymbol]) {
            val sym = lst.head.value.asInstanceOf[CExpr.SSymbol];
            if(sym.value != "fn") { _searchMaxSymbolList(lastCount,lst) } else { 0 }
          } else { 0 }
        }
        case CExpr.SVector(lst) => _searchMaxSymbolList(lastCount,lst)
        case CExpr.SMap(lst) => _searchMaxSymbolList(lastCount,lst)
        case CExpr.SXMLElement(_, attrList, child) => {
          val maxAttr = attrList.map(attrV => searchMaxSymbol(lastCount,attrV._2.value)).max;
          var retCount = if(maxAttr > lastCount) maxAttr else lastCount
          for(expr <- child) {
            val count = searchMaxSymbol(lastCount,expr.value)
            if(count > retCount) retCount = count
          }
          retCount
        }
        case _ => 0
    }

    private def _searchMaxSymbolList(lastCount:Int,lst:ArrayBuffer[TextSpan[CExpr]]):Int = {
      var retCount = lastCount
      for(lstExpr <- lst) {
        val count = searchMaxSymbol(lastCount,lstExpr.value)
        if(count > retCount) retCount = count
      }
      retCount
    }

    protected def translateVector(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try { 
        TextSpan(pos,VMExpr.VMArray(lst.map(translate(_).get).toVector))
    }

    protected def translateXMLElement(pos:SpanPos,tag:String,attrList:ArrayBuffer[(String,TextSpan[CExpr])],
                                      child:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      var vmAttrList:Vector[(String,TextSpan[VMExpr])] = attrList.map((k,v) => (k,translate(v).get)).toVector
      
      TextSpan(pos,VMExpr.VMXml(tag,vmAttrList,child.map(v => translate(v).get ).toVector))
    }

    protected def translateMap(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
        TextSpan(pos,VMExpr.VMMap(lst.map(translate(_).get).toVector))
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
        case "defn" => Some(translateDeFn(pos,lst).get)
        case "let" => Some(translateLet(pos,lst,false).get)
        case "do" => Some(translateDo(pos,lst).get)
        case "loop" => Some(translateLet(pos,lst,true).get)
        case "recur" => Some(translateRecur(pos,lst).get)
        case "match" => None
        case "export" => Some(translateExport(pos,lst).get)
        case "import" => Some(translateImport(pos,lst).get)
        case _ => None
    }

    protected def translateImport(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      for(expr <- lst.tail) {
        expr.value match
          case CExpr.SSymbol(_, name) => {
            this.importInfos.addOne(ImportInfo(name))
          }
          case _ => {}
      }
      TextSpan(pos,VMExpr.VMImport)
    }

    protected def translateExport(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      for(expr <- lst.tail) {
        expr.value match
          case CExpr.SSymbol(_, name) => {
            this.exportSymbols.addOne(name)
          }
          case _ => {}
      }
      TextSpan(pos,VMExpr.VMExport)
    }

    protected def translateInvoke(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val fnExpr = translate(lst.head).get
      var argsExpr:Vector[TextSpan[VMExpr]] = Vector.empty
      for(idx <- 1.until(lst.length)) {
        argsExpr = argsExpr :+ translate(lst(idx)).get
      }
      TextSpan(pos,VMExpr.VMCall(fnExpr,argsExpr))
    }

    protected def translateIf(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      if(lst.length < 3 || lst.length > 4) throw InvalidIf(pos)
      val condExpr = translate(lst(1)).get
      val trueExpr = translate(lst(2)).get
      var alts:Vector[Alternative] = Vector(Alternative(AltPattern.Literal(LitValue.LBool(true)),trueExpr))
      if(lst.length > 3) {
        val falseExpr = translate(lst(3)).get
        alts = alts.appended(Alternative(AltPattern.Literal(LitValue.LBool(false)),falseExpr))
      } else {
        alts = alts.appended(Alternative(AltPattern.Literal(LitValue.LBool(false)),TextSpan(pos,VMExpr.VMNil)))
      }
      TextSpan(pos,VMExpr.VMMatch(condExpr,alts))
    }

    protected def translateDef(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val defExpr = translate(lst(1)).get
      defExpr.value match
        case VMExpr.VMSymbol(symbol) => {
          val valueExpr = translate(lst(2)).get
          TextSpan(pos,VMExpr.VMDef(symbol,valueExpr))
        }
        case _ => throw InvalidDef(pos)
    }
    
    protected def translateFn(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      //(fn [a b] ...)
      if(lst.length < 2) throw InvalidFN(pos)
      val argSyms:Vector[Symbol] = this.takeArrayArgSyms(pos,lst(1).value).get
      var bodyList:Vector[TextSpan[VMExpr]] = Vector.empty
      for(idx <- 2.until(lst.length)) {
        bodyList = bodyList :+ translate(lst(idx)).get
      }
      if(bodyList.isEmpty) {
        bodyList = bodyList.appended(TextSpan(pos,VMExpr.VMNil))
      }
      TextSpan(pos,VMExpr.VMFunc(argSyms,bodyList))
    }

    protected def takeArrayArgSyms(pos:SpanPos,cExpr:CExpr):Try[Vector[Symbol]] = Try {
      cExpr match
        case CExpr.SVector(args) => {
          var argSyms:Vector[Symbol] = Vector.empty
          val argsList = args.map(v => translate(v).get)
          for(arg <- argsList) {
            arg.value match
              case VMExpr.VMSymbol(value) => argSyms = argSyms.appended(value)
              case _ => throw InvalidList(pos)
          }
          argSyms
        }
        case _ => throw InvalidList(pos)
    }

    protected def translateDeFn(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      //(defn name [args1 args2] ...)
      val nameSymbol = translateCastTo[VMExpr.VMSymbol](lst(1)).get.getOrElse(throw InvalidDefFN(pos))
      val argSyms = this.takeArrayArgSyms(pos,lst(2).value).get

      var bodyList:Vector[TextSpan[VMExpr]] = Vector.empty
      for(idx <- 3.until(lst.length)) {
        val expr = translate(lst(idx)).get
        bodyList = bodyList.appended(expr)
      }
      if(bodyList.isEmpty) {
        bodyList = bodyList.appended(TextSpan(pos,VMExpr.VMNil))
      }
      TextSpan(pos,VMExpr.VMDef(nameSymbol.value,TextSpan(pos,VMExpr.VMFunc(argSyms,bodyList))))
    }

    protected def translateRecur(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val exprList = this.takeBodyList(pos,1,lst).get
      TextSpan(pos,VMExpr.VMRecur(exprList))
    }

    protected def translateLet(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]],isLoop:Boolean):Try[TextSpan[VMExpr]] = Try {
      val lets = translateCastTo[VMExpr.VMArray](lst(1)).get.getOrElse(throw InvalidLetFN(pos))
      val bodyList = this.takeBodyList(pos,2,lst).get
      TextSpan(pos,VMExpr.VMLet(lets.value,bodyList,isLoop))
    }

    protected def translateDo(pos:SpanPos,lst:ArrayBuffer[TextSpan[CExpr]]):Try[TextSpan[VMExpr]] = Try {
      val bodyList = this.takeBodyList(pos,1,lst).get
      TextSpan(pos,VMExpr.VMLet(Vector.empty,bodyList,false))
    }

    protected def takeBodyList(pos:SpanPos,offsetIdx:Int,lst:ArrayBuffer[TextSpan[CExpr]]):Try[Vector[TextSpan[VMExpr]]] = Try {
      var bodyList:Vector[TextSpan[VMExpr]] = Vector.empty
      for(idx <- offsetIdx.until(lst.length)) {
        val expr = translate(lst(idx)).get
        bodyList = bodyList.appended(expr)
      }
      if(bodyList.isEmpty) {
        bodyList = bodyList.appended(TextSpan(pos,VMExpr.VMNil))
      }
      bodyList
    }

    inline protected def translateCastTo[T <: VMExpr](cExpr:TextSpan[CExpr]):Try[Option[T]] = Try {
      vmExprCastTo[T](translate(cExpr).get.value)
    }
}

