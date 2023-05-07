package ui
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import _root_.core.reflect.Assembly

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

case class BindingItem(sourceType:BindingSource,sourceKey:String,dstKey:String,conv:Option[PropertyConverter]);

object BindingItem {
  def parse(value:String,dstKey:String):BindingItem = {
    //{Binding Owner checked  ui.BoolAtlasSprite(default.duikong,default.duihao)}
    val startLen = "{Binding".length();
    val remainString = value.substring(startLen,value.length() - 1).trim();
    var args = remainString.split(' ');
    
    val sourceType = args(0) match
      case "Data"  => BindingSource.Data
      case "Owner" => BindingSource.Owner
      case _ => BindingSource.Data
    val srcKey = args(1);
   
    var conv:Option[PropertyConverter] = None;
    if(args.length > 2) {
      conv = this.parseConverter(args(2))
    }
    BindingItem(sourceType,srcKey,dstKey,conv)
  }

  def parseConverter(value:String):Option[PropertyConverter] = {
    //ui.BoolAtlasSprite(default.duikong,default.duihao)
    val strs = value.split('(');
    if(strs(0).isEmpty() || strs.length < 2) return None;
    val convName = strs(0);
    val args = strs(1).substring(0,strs(1).length() - 1).split(',');
    val convInst = Assembly.createInstance(convName);
    if(convInst.isEmpty) return None;
    val converter = convInst.get.asInstanceOf[PropertyConverter];
    converter.init(args);
    Some(converter)
  }
}