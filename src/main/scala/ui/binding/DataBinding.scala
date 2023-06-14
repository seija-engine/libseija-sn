package ui.binding

import core.reflect.Assembly
import scala.util.Try
import core.reflect.NotFoundTypeInfoException
import core.reflect.NotFoundFieldException
import ui.binding.INotifyPropertyChanged;
import scala.collection.mutable.ArrayBuffer
import core.reflect.{TypeInfo,FieldInfo}
import core.reflect.DynTypeConv
import scala.util.Failure
import scala.util.Success

object DataBindingManager {
  var instList:ArrayBuffer[BindingInst] = ArrayBuffer.empty

  def binding(srcObject:Any,dstObject:Any,item:BindingItem): Try[Option[BindingInst]] = Try {
    if(srcObject == null || dstObject == null) {
      throw new Exception("DataBinding srcObject or dstObject is null")
    }
    var retInst:Option[BindingInst] = None;
    item.typ match {
      case BindingType.Src2Dst => {
        if(srcObject.isInstanceOf[INotifyPropertyChanged] && item.sourceKey != "this") {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
          retInst = Some(inst)
        } else { 
          this.applyOnce(srcObject,dstObject,item) 
        }
      }
      case BindingType.Dst2Src => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
          retInst = Some(inst)
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
      case BindingType.Both => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged] && srcObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList.addOne(inst);
          retInst = Some(inst)
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
    }
    retInst
  }

  def applyOnce(srcObject:Any,dstObject:Any,item:BindingItem):Unit = {
      
      if(item.typ == BindingType.Src2Dst || item.typ == BindingType.Both) {
        var srcValue = srcObject;
        if(item.sourceKey != "this") {
          val srcTypeInfo = Assembly.getTypeInfoOrThrow(srcObject);
          srcValue = srcTypeInfo.getValue_?(srcObject,item.sourceKey);
        }
        if(item.conv.isDefined) { srcValue = item.conv.get.conv(srcValue) }
        val dstTypeInfo = Assembly.getTypeInfoOrThrow(dstObject);
        dstTypeInfo.setValue(dstObject,item.dstKey,srcValue);
      } else {
        var dstValue = dstObject;
        if(item.dstKey != "this") {
          val dstTypeInfo = Assembly.getTypeInfoOrThrow(dstObject);
          dstValue = dstTypeInfo.getValue_?(dstObject,item.dstKey);
        }
        if(item.conv.isDefined) { dstValue = item.conv.get.conv(dstValue) }
        val srcTypeInfo = Assembly.getTypeInfoOrThrow(srcObject);
        srcTypeInfo.setValue(srcObject,item.sourceKey,dstValue);
      }
  }


  def removeByDst(dstObject:Any): Unit = {
     for(idx <- this.instList.length - 1 to 0 by -1) {
       if(this.instList(idx).dstObject == dstObject) {
         this.instList(idx).release();
         this.instList.remove(idx);
       }
     }
  }

  def removeInst(inst:BindingInst):Unit = {
    val idx = this.instList.indexOf(inst);
    if(idx >= 0) {
      this.instList(idx).release();
      this.instList.remove(idx);
    }
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
     item.typ match
      case BindingType.Src2Dst => {
        val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
        srcNotity.addPropertyChangedHandler(this.onSrcPropertyChanged,null)
        this.setSrc2Dst(srcObject);
      }
      case BindingType.Dst2Src => {
        val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
        dstNotity.addPropertyChangedHandler(this.onDstPropertyChanged,null)
        this.setDst2Src(dstObject);
      }        
      case BindingType.Both => {
        this.setSrc2Dst(srcObject);
         val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
         val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
         srcNotity.addPropertyChangedHandler(this.onSrcPropertyChanged,null);
         dstNotity.addPropertyChangedHandler(this.onDstPropertyChanged,null);
      }
  }

  def onSrcPropertyChanged(sender:INotifyPropertyChanged,name:String,sourceObj:Any,param:Any): Unit = {
    if(name == this.srcField.Name) {
      this.setSrc2Dst(sourceObj);
    }
  }

  def onDstPropertyChanged(sender:INotifyPropertyChanged,name:String,sourceObj:Any,param:Any): Unit = {
      if(name == this.dstField.Name) {
        this.setDst2Src(sourceObj);
      }
  }

  def setSrc2Dst(sourceObj:Any):Unit = {
      var setValue = this.srcField.get(this.srcObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      if(setValue.getClass().getName() != this.dstField.Name) {
        DynTypeConv.convertStrTypeTry(setValue.getClass().getName(),this.dstField.typName,setValue) match {
          case Success(value) => {
            setValue = value;
          }
          case Failure(exception) => {
            System.err.println(exception.toString());
            return;
          }
        }
      }
      dstTypeInfo.setValue(dstObject,this.item.dstKey,setValue)
      if(dstObject != sourceObj && dstObject.isInstanceOf[INotifyPropertyChanged]) {
          dstObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.dstKey,sourceObj)
      }
  }

  def setDst2Src(sourceObj:Any):Unit = {
      var setValue = this.dstField.get(this.dstObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      srcTypeInfo.setValue(srcObject,this.item.sourceKey,setValue)
      if(srcObject != sourceObj && srcObject.isInstanceOf[INotifyPropertyChanged]) {
          srcObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.sourceKey,sourceObj)
      }
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
    val srcTypeInfo = Assembly.getTypeInfoOrThrow(src);
    val dstTypeInfo = Assembly.getTypeInfoOrThrow(dst);
    val srcField:FieldInfo = srcTypeInfo.getFieldTry(item.sourceKey).get;
    val dstField:FieldInfo = dstTypeInfo.getFieldTry(item.dstKey).get;
    BindingInst(item,src,dst,srcTypeInfo,dstTypeInfo,srcField,dstField)
  }
}