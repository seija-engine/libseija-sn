package com.seija.core.reflect
import scala.quoted._;
/*

import scala.annotation.MacroAnnotation;
import scala.annotation.experimental;
@experimental
class AutoGetSetter extends MacroAnnotation  {
  override def transform(using Quotes)(tree:quotes.reflect.Definition): List[quotes.reflect.Definition] = {
    List(tree)
  }
}

transparent inline def autoProps[T](inline x:T) = ${autoPropsImpl[T]('x)}

def autoPropsImpl[T:Type](thisValue:Expr[T])(using Quotes):Expr[Any] = {
    import quotes.reflect.*
    val typRepr = TypeRepr.of[T];
    val refi =  typRepr.typeSymbol.declaredFields.foldLeft(typRepr)((parentTypeExpr,sym) => {
        val capName = sym.name.capitalize;
        sym.typeRef.asType match
            case '[st] => 
                val setMethodType = MethodType(List("value"))(_ => List(TypeRepr.of[st]),_ => TypeRepr.of[Unit]);
                val getMethodType = MethodType(List())(_ => List(),_ => TypeRepr.of[st]);
                Refinement(Refinement(parentTypeExpr,s"set${capName}",setMethodType),s"get${capName}",getMethodType)
    })
    
    val endExpr = refi.asType match {
        case '[t] => '{${thisValue}.asInstanceOf[t]}
        case _ => report.error("notityPropsImpl: not a refinement type");  '{null}
    }
    endExpr
}
*/