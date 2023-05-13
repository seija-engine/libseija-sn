package ui.binding
import scala.collection.mutable.HashMap
import scala.annotation.{MacroAnnotation,experimental};
 import scala.quoted.*

type PropertyChangedCallBack = (INotifyPropertyChanged,String,Any,Any) => Unit;

trait INotifyPropertyChanged {
  var handles:HashMap[PropertyChangedCallBack,Any] = HashMap()

  def addPropertyChangedHandler(handler:PropertyChangedCallBack,param:Any): Unit = {
    this.handles.put(handler,param)
  }

  def removePropertyChangedHandler(handler:PropertyChangedCallBack): Unit = {
    this.handles.remove(handler)
  }

  def callPropertyChanged(propertyName: String,sourceObj:Any): Unit = {
    this.onPropertyChanged(propertyName)
    for(kv <- this.handles) {
       kv._1.apply(this,propertyName,sourceObj,kv._2)
    }
  }

  def onPropertyChanged(propertyName:String):Unit = {}

}

 @experimental
class AutoChangedGetSetter extends MacroAnnotation {
  def transform(using Quotes)(tree: quotes.reflect.Definition): List[quotes.reflect.Definition] = {
    import quotes.reflect._
    tree match {
      case ClassDef(className, ctr, parents, self, body) => {
        val cls = tree.symbol
        val fields = body.collect {  case v:ValDef => v }
        var allInfo = "";
        fields.foreach(f => allInfo += f.name);
        report.info(allInfo)
        val newDef = '{ def TestFunc():Unit = {} }.asTerm
        val newBody =  newDef :: body
        List(ClassDef.copy(tree)(className, ctr, parents, self, newBody))
      }
      case _ => {
        report.error("only supported class")
        List(tree)
      }
    }
    List(tree)
  } 

}