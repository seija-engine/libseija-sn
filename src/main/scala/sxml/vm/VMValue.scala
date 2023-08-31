package sxml.vm
import scala.quoted.*
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashMap

enum VMValue {
    case  NIL()
    case  VMChar(value:Char)
    case  VMLong(value:Long)
    case  VMFloat(value:Double)
    case  VMString(value:String)
    case  VMKeyword(value:String)
    case  VMArray(value:Vector[VMValue])
    case  VMMap(value:HashMap[VMValue,VMValue])
    case  VMClosure(data:ClosureData)
    case  VMExternFunc(data:ExternFuncData)
    case  VMXml(value:XmlNode)
    case  VMUnWrap(value:Vector[VMValue])

    def isFloat():Boolean = {
        this match
            case VMFloat(value) => true
            case _ => false
    }

    def castFloat():Option[Double] = {
        this match
            case VMLong(value) => Some(value.toDouble)
            case VMFloat(value) => Some(value) 
            case _ => None
    }

    def castInt():Option[Long] = {
        this match
            case VMLong(value) => Some(value)
            case VMFloat(value) => Some(value.toInt)
            case _ => None
    }

    inline def unwrap[T]():Option[T] = {
        this.match
            case v:T => Some(v)
            case _ => None
    }

    override def equals(other: Any): Boolean = {
        if(!other.isInstanceOf[VMValue]) return false
        val otherValue = other.asInstanceOf[VMValue]
        this.match
            case NIL() => otherValue.unwrap[VMValue.NIL]().isDefined
            case VMChar(value) =>  otherValue.unwrap[VMValue.VMChar]().map(v => v.value == value).getOrElse(false)
            case VMLong(value) =>  otherValue.unwrap[VMValue.VMLong]().map(v => v.value == value).getOrElse(false)
            case VMFloat(value) => otherValue.unwrap[VMValue.VMFloat]().map(v => v.value == value).getOrElse(false)
            case VMString(value) => otherValue.unwrap[VMValue.VMString]().map(v => v.value == value).getOrElse(false)
            case VMKeyword(value) => otherValue.unwrap[VMValue.VMKeyword]().map(v => v.value == value).getOrElse(false)
            case VMArray(thisList) =>  {
               val otherList = otherValue.unwrap[VMValue.VMArray]()
               if(otherList.isEmpty) return false
               for((av,bv) <- thisList.zip(otherList.get.value)) {
                if(!av.equals(bv)) return false
               }
               true
            }
            case _ => false
    }

    override def toString(): String = {
        this match
            case NIL() => "nil"
            case VMChar(value) => value.toString()
            case VMLong(value) => value.toString()
            case VMFloat(value) =>  value.toString()
            case VMString(value) => value
            case VMKeyword(value) => value
            case VMArray(value) => "<array>"
            case VMMap(value) => "<map>"
            case VMClosure(data) => "<closure>"
            case VMExternFunc(data) => "<func>"
            case VMXml(value) => "<xml>"
            case VMUnWrap(value) => "<unwrap>"
        
    }
}

type VMFuncType = (ctx:VMCallStack) => Unit;

case class ClosureData(val function:CompiledFunction,upvars:ArrayBuffer[VMValue])

case class XmlNode(val Name:String,
                   val attrs:HashMap[String,VMValue],
                   val child:Vector[VMValue]);

case class ExternFuncData(val name:String,func:VMFuncType) {
    def call(stack:VMCallStack):Unit = { func(stack) }
}

inline def wrapExternFunc[T](inline f:T):ExternFuncData =  ${wrapExternFuncImpl('f) }

def wrapExternFuncImpl[T](expr:Expr[T])(using Type[T])(using Quotes):Expr[ExternFuncData] = {
    import quotes.reflect.*;
    val repr = TypeRepr.of[T]
    var argsCount = 0;
    var fnName = "";
    var fnNameTerm:Option[Term] = None;
    expr.asTerm match
        case Inlined(a,b,Inlined(_,_,Block(lst,t))) => {
            lst(0) match
                case DefDef(_,_,_,Some(Apply(id@Ident(name),args))) => {
                    fnName = name;
                    fnNameTerm = Some(id);
                    argsCount = args.length
                }
                case _ =>   
        }
        case _ => 
    
    val wrapFunc = '{
      def _wrap(stack:VMCallStack):Unit = {
        val ret = ${
            val popExpr = '{stack.pop()};
            val lst = 0.until(argsCount).map(_ => popExpr.asTerm ).toList;
            Apply(fnNameTerm.get,lst).asExpr
        }
        stack.pop();
        stack.push(ret.asInstanceOf[VMValue])
      }
      _wrap
    };
    
    val retExpr = '{ExternFuncData(${Expr(fnName)},${wrapFunc})}
    report.info(retExpr.show) 
    retExpr
}