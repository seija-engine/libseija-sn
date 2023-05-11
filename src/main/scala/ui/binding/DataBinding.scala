package ui.binding

import core.reflect.Assembly
import scala.util.Try
import core.reflect.NotFoundTypeInfoException
import core.reflect.NotFoundFieldException
import ui.binding.INotifyPropertyChanged;

object DataBindingManager {
  
  def binding(srcObject:Any,dstObject:Any,item:BindingItem): Try[Unit] = Try {
    assert(srcObject != null && dstObject != null);

    item.typ match {
      case BindingType.Src2Dst => {
        this.setValue(srcObject, dstObject, item);
        if(srcObject.isInstanceOf[INotifyPropertyChanged]) {
          val srcNotify = srcObject.asInstanceOf[INotifyPropertyChanged];
          println(srcNotify)
        }
      }
      case BindingType.Dst2Src => {
        this.setValue(dstObject, srcObject, item)
      }
      case BindingType.Both => {
        this.setValue(srcObject, dstObject, item)
      }
    }
  }

  protected def setValue(srcObject:Any,dstObject:Any,item:BindingItem) = {
    val srcType = Assembly.getTypeInfo_?(srcObject);
    val dstType = Assembly.getTypeInfo_?(dstObject);
    val srcRawValue = if(item.sourceKey != "this") { srcType.GetValue_?(srcObject,item.sourceKey) } else { srcObject }
    val srcValue = item.conv match {
      case Some(convter) => convter.conv(srcRawValue)
      case None => srcRawValue
    }
    dstType.GetField_?(item.dstKey).set(dstObject,srcValue)
  }


  def removeByDst(dstObject:Any): Unit = {
      
  }

}
