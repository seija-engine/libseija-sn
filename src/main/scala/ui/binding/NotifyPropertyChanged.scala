package ui.binding
import scala.collection.mutable.HashMap

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