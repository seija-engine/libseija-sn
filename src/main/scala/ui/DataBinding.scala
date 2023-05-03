package ui
import scala.collection.mutable.ListBuffer

trait INotifyPropertyChanged {
  var propertyChangedHandlers: ListBuffer[(INotifyPropertyChanged, PropertyChangedEventArgs) => Unit] = ListBuffer()

  def addPropertyChangedHandler(handler: (INotifyPropertyChanged, PropertyChangedEventArgs) => Unit): Unit = {
    propertyChangedHandlers += handler
  }

  def removePropertyChangedHandler(handler: (INotifyPropertyChanged, PropertyChangedEventArgs) => Unit): Unit = {
    propertyChangedHandlers -= handler
  }

  def callPropertyChanged(propertyName: String,newValue:Any): Unit = {
    propertyChangedHandlers.foreach(_.apply(this, new PropertyChangedEventArgs(propertyName,newValue)))
  }

  def onPropertyChanged(propertyName:String):Unit = {}

}

case class PropertyChangedEventArgs(val propertyName: String,val newValue:Any)


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