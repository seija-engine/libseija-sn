package com.seija.ui.binding
import scala.collection.mutable.HashMap
import scala.quoted.*
import scala.collection.mutable.ArrayBuffer
import com.seija.core.reflect.{TypeInfo, FieldInfo}
import scala.collection.mutable.HashSet
import scala.util.Try
import scala.util.Success
import com.seija.core.reflect.DynTypeConv
import scala.util.Failure

case class ChangedHandle(
  handleObject:Any,
  callback:PropertyChangedCallBack,
  param:Any = null
)

case class LinkObjectInfo(
  linkObject:INotifyPropertyChanged,
  srcProperty:FieldInfo,
  dstProperty:FieldInfo
)

type PropertyChangedCallBack = (INotifyPropertyChanged, String, Any) => Unit;

trait INotifyPropertyChanged {
  val handleList:ArrayBuffer[ChangedHandle] = ArrayBuffer()
  val linkObjectList:ArrayBuffer[LinkObjectInfo] = ArrayBuffer.empty

  def addHandle(handle:ChangedHandle):Unit = {
    this.handleList += handle
  }

  def removeHandle(callback:PropertyChangedCallBack):Unit = {
    val idx = this.handleList.indexWhere(_.callback == callback)
    if(idx >= 0) { this.handleList.remove(idx) }
  }

  def linkNotifyObject(dstObject:INotifyPropertyChanged,srcProperty:FieldInfo,dstProperty:FieldInfo):Unit = {
    val info = LinkObjectInfo(dstObject,srcProperty,dstProperty)
    this.linkObjectList += info
  }

  def unLinkNotifyObject(dstObject:INotifyPropertyChanged,srcProperty:FieldInfo,dstProperty:FieldInfo):Unit = {
    val idx = this.linkObjectList.indexWhere(v => v.linkObject == dstObject && v.srcProperty == srcProperty && v.dstProperty == dstProperty)
    if(idx >= 0) { this.linkObjectList.remove(idx) }
  }

  def callPropertyChanged(propertyName: String,callSelfChanged:Boolean = true): Unit = {
    if(callSelfChanged) { this.onPropertyChanged(propertyName) }
    this.handleList.foreach {handle => 
      handle.callback(this,propertyName,handle.param)  
    }
    if(!this.linkObjectList.isEmpty) {
      val callSets:HashSet[LinkObjectInfo] = HashSet.empty
      this.deepCallLinkObjects(callSets,propertyName,this,propertyName);
    }
  }

  private def deepCallLinkObjects(
    callSets:HashSet[LinkObjectInfo],propertyName:String,
    srcObject:INotifyPropertyChanged,srcPropertyName:String):Unit = {
     for(linkInfo <- this.linkObjectList) {
       if(!callSets.contains(linkInfo) && linkInfo.srcProperty.Name == propertyName && 
        !(linkInfo.linkObject == srcObject && linkInfo.dstProperty.Name == srcPropertyName)) {
         callSets += linkInfo

         var srcValue = linkInfo.srcProperty.get(this);
         this.tryConvValue(srcValue,linkInfo.dstProperty) match
           case Failure(exception) => slog.error(exception)
           case Success(value) => srcValue = value
         linkInfo.dstProperty.set(linkInfo.linkObject,srcValue)
         linkInfo.linkObject.onPropertyChanged(linkInfo.dstProperty.Name)
         linkInfo.linkObject.deepCallLinkObjects(callSets,linkInfo.dstProperty.Name,srcObject,srcPropertyName)
       }
     }
  }

  private def tryConvValue(srcValue:Any,dstField:FieldInfo):Try[Any] = {
    if(srcValue == null) return Success(srcValue)
    val srcValueTypName = srcValue.getClass().getName();
    if(srcValueTypName != dstField.Name) {
        if(srcValue.isInstanceOf[scala.collection.IndexedSeq[Any]] && dstField.typName == "scala.collection.IndexedSeq[scala.Any]") {
           return Success(srcValue);
        }
        DynTypeConv.convertStrTypeTry(srcValueTypName,dstField.typName,srcValue)
    } else {
        Success(srcValue)
    }
  }

  def onPropertyChanged(propertyName: String): Unit = {}
}


/*
//import scala.annotation.{MacroAnnotation, experimental};
trait BProp[T] {
  var value: T;
  val parent:INotifyPropertyChanged;
  def set(v: T): Unit;
}

inline def autoProp[T](p:INotifyPropertyChanged,fieldName: String, default: T) = ${autoPropImpl[T]('p,'fieldName, 'default)}

def autoPropImpl[T](base: Expr[INotifyPropertyChanged],fieldName: Expr[String], default: Expr[T])
                   (using Type[T])(using Quotes): Expr[BProp[T]] = {
  '{
    var newProp = new BProp[T] {
      var value: T = $default;
      val parent: INotifyPropertyChanged = ${base};
      def set(setValue: T) = {
        this.value = setValue;
        this.parent.callPropertyChanged($fieldName, this.parent)
      }
    }
    newProp
  }
}*/
