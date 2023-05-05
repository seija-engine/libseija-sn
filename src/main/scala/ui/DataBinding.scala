package ui
import scala.collection.mutable.ListBuffer
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

  def callPropertyChanged(propertyName: String,newValue:Any): Unit = {
    this.onPropertyChanged(propertyName)
    for(kv <- this.handles) {
       kv._1.apply(this,propertyName,newValue,kv._2)
    }
  }

  def onPropertyChanged(propertyName:String):Unit = {}

}



enum BindingSource(val value:Int) {
  case Owner extends BindingSource(0)
  case Data  extends BindingSource(1)
}

case class BindingItem(sourceType:BindingSource,sourceKey:String,dstKey:String);

object BindingItem {
  def parse(value:String,dstKey:String):BindingItem = {
    //{Binding Owner }
    val startLen = "{Binding".length();
    val remainString = value.substring(startLen,value.length() - 1).trim();
    var args = remainString.split(' ');
    val sourceType = args(0) match
      case "Data"  => BindingSource.Data
      case "Owner" => BindingSource.Owner
      case _ => BindingSource.Data
    val srcKey = args(1);
    BindingItem(sourceType,srcKey,dstKey)
  }
}