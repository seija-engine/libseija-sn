package ui.binding

import core.reflect.Assembly
import scala.util.Try
import core.reflect.NotFoundTypeInfoException
import core.reflect.NotFoundFieldException
import ui.binding.INotifyPropertyChanged;
import scala.collection.mutable.ArrayBuffer
import core.reflect.{TypeInfo,FieldInfo}

object DataBindingManager {
  var instList:ArrayBuffer[BindingInst] = ArrayBuffer.empty

  def binding(srcObject:Any,dstObject:Any,item:BindingItem): Try[Unit] = Try {
    assert(srcObject != null && dstObject != null);
    item.typ match {
      case BindingType.Src2Dst => {
        if(srcObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
      case BindingType.Dst2Src => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
      case BindingType.Both => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged] && srcObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
    }
  }

  def applyOnce(srcObject:Any,dstObject:Any,item:BindingItem):Unit = {
      if(item.typ == BindingType.Src2Dst || item.typ == BindingType.Both) {
        var srcValue = srcObject;
        if(item.sourceKey != "this") {
          val srcTypeInfo = Assembly.getTypeInfo_?(srcObject);
          srcValue = srcTypeInfo.GetValue_?(srcObject,item.sourceKey);
        }
        if(item.conv.isDefined) { srcValue = item.conv.get.conv(srcValue) }
        val dstTypeInfo = Assembly.getTypeInfo_?(dstObject);
        dstTypeInfo.setValue(dstObject,item.dstKey,srcValue);
      } else {
        var dstValue = dstObject;
        if(item.dstKey != "this") {
          val dstTypeInfo = Assembly.getTypeInfo_?(dstObject);
          dstValue = dstTypeInfo.GetValue_?(dstObject,item.dstKey);
        }
        if(item.conv.isDefined) { dstValue = item.conv.get.conv(dstValue) }
        val srcTypeInfo = Assembly.getTypeInfo_?(srcObject);
        srcTypeInfo.setValue(srcObject,item.sourceKey,dstValue);
      }
  }


  def removeByDst(dstObject:Any): Unit = {
      
  }
}

case class BindingInst(
  val item:BindingItem,
  val srcObject:Any,
  val dstObject:Any,
  val srcTypeInfo:TypeInfo,
  val dstTypeInfo:TypeInfo,
  val srcField:FieldInfo,
  val dstField:FieldInfo
) {
  
  def init(): Unit = {
     println(s"init ${item}")
     item.typ match
      case BindingType.Src2Dst => {
        val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
        srcNotity.addPropertyChangedHandler(this.onSrcPropertyChanged,null)
        this.setSrc2Dst();
      }
      case BindingType.Dst2Src => {
        val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
        dstNotity.addPropertyChangedHandler(this.onDstPropertyChanged,null)
        this.setDst2Src();
      }        
      case BindingType.Both => {
        this.setSrc2Dst();
         val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
         val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
         srcNotity.addPropertyChangedHandler(this.onSrcPropertyChanged,null);
         dstNotity.addPropertyChangedHandler(this.onDstPropertyChanged,null);
      }
  }

  def onSrcPropertyChanged(sender:INotifyPropertyChanged,name:String,newValue:Any,param:Any): Unit = {
      this.setSrc2Dst();
  }

  def onDstPropertyChanged(sender:INotifyPropertyChanged,name:String,newValue:Any,param:Any): Unit = {
      this.setDst2Src();
  }

  def setSrc2Dst():Unit = {
      var setValue = this.srcField.get(this.srcObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      dstTypeInfo.setValue(dstObject,this.item.dstKey,setValue)
  }

  def setDst2Src():Unit = {
      var setValue = this.dstField.get(this.dstObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      srcTypeInfo.setValue(srcObject,this.item.sourceKey,setValue)
  }

  def release(): Unit = {
    item.typ match
      case BindingType.Src2Dst => {
        val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
        srcNotity.removePropertyChangedHandler(this.onSrcPropertyChanged)
      }
      case BindingType.Dst2Src =>
      case BindingType.Both =>
  }
}

object BindingInst {
  def create(src:Any,dst:Any,item:BindingItem):Try[BindingInst] = Try {
    val srcTypeInfo = Assembly.getTypeInfo_?(src);
    val dstTypeInfo = Assembly.getTypeInfo_?(dst);
    val srcField = srcTypeInfo.GetField_?(item.sourceKey);
    val dstField = dstTypeInfo.GetField_?(item.dstKey);
    BindingInst(item,src,dst,srcTypeInfo,dstTypeInfo,srcField,dstField)
  }
}