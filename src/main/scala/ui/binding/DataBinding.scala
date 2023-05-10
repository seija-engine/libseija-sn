package ui.binding

import core.reflect.Assembly
import scala.util.Try

object DataBindingManager {
  
  def binding(srcObject:Any,dstObject:Any,item:BindingItem): Unit = {
    if(srcObject == null || dstObject == null) return;
    item.typ match {
      case BindingType.Src2Dst => {
        
      }
      case BindingType.Dst2Src => {

      }
      case BindingType.Both => {

      }
    }
  }

  def setValue(srcObject:Any,dstObject:Any,item:BindingItem) = {
    val srcType = Assembly.getTypeInfo(srcObject);
    true
  }

  protected def applyOnce(srcObject:Any,srcKey:Option[String],dstObject:Any,dstKey:String,conv:Option[PropertyConverter]):Unit = {

  }

  def removeByDst(dstObject:Any): Unit = {
      
  }

}
