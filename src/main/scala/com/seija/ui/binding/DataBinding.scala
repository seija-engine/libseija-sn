package com.seija.ui.binding
import com.seija.core.reflect.Assembly
import scala.util.Try
import com.seija.core.reflect.NotFoundTypeInfoException
import com.seija.core.reflect.NotFoundFieldException
import com.seija.ui.binding.INotifyPropertyChanged;
import scala.collection.mutable.ArrayBuffer
import com.seija.core.reflect.{TypeInfo, FieldInfo}
import com.seija.core.reflect.DynTypeConv
import scala.util.Failure
import scala.util.Success
import com.seija.core.logError;

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
          this.instList += inst;
          retInst = Some(inst)
        } else { 
          this.applyOnce(srcObject,dstObject,item) 
        }
      }
      case BindingType.Dst2Src => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList += inst;
          retInst = Some(inst)
        } else { this.applyOnce(srcObject,dstObject,item) }
      }
      case BindingType.Both => {
        if(dstObject.isInstanceOf[INotifyPropertyChanged] && srcObject.isInstanceOf[INotifyPropertyChanged]) {
          var inst = BindingInst.create(srcObject, dstObject, item).get;
          inst.init();
          this.instList += inst;
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
        if(dstObject.isInstanceOf[INotifyPropertyChanged]) {
          dstObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.dstKey);
        }
      } else {
        var dstValue = dstObject;
        if(item.dstKey != "this") {
          val dstTypeInfo = Assembly.getTypeInfoOrThrow(dstObject);
          dstValue = dstTypeInfo.getValue_?(dstObject,item.dstKey);
        }
        if(item.conv.isDefined) { dstValue = item.conv.get.conv(dstValue) }
        val srcTypeInfo = Assembly.getTypeInfoOrThrow(srcObject);
        srcTypeInfo.setValue(srcObject,item.sourceKey,dstValue);
        if(srcObject.isInstanceOf[INotifyPropertyChanged]) {
          srcObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.sourceKey);
        }
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
        dstObject match
          case dstNotityObject:INotifyPropertyChanged => {
            srcNotity.linkNotifyObject(dstNotityObject,srcField,dstField)
            srcNotity.callPropertyChanged(srcField.Name)
          }
          case _ => {
            srcNotity.addHandle(ChangedHandle(dstObject,onSrcPropertyChanged,null))
            this.setSrc2Dst() 
          }
      }
      case BindingType.Dst2Src => {
        val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
        srcObject match
          case srcNotity:INotifyPropertyChanged => {
            dstNotity.linkNotifyObject(srcNotity,dstField,srcField)
            dstNotity.callPropertyChanged(dstField.Name)
          }
          case _ => {
            dstNotity.addHandle(ChangedHandle(srcObject,onDstPropertyChanged,null))
            this.setDst2Src()
          }       
      }        
      case BindingType.Both => {
         val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
         val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
         srcNotity.linkNotifyObject(dstNotity,srcField,dstField)
         dstNotity.linkNotifyObject(srcNotity,dstField,srcField)
         srcNotity.callPropertyChanged(srcField.Name)
      }
  }

  def onSrcPropertyChanged(sender:INotifyPropertyChanged,name:String,param:Any): Unit = {
    if(name == this.srcField.Name) {
      this.setSrc2Dst();
    }
  }

  def onDstPropertyChanged(sender:INotifyPropertyChanged,name:String,param:Any): Unit = {
      if(name == this.dstField.Name) {
        this.setDst2Src();
      }
  }

  def setSrc2Dst():Unit = {
      var setValue = this.srcField.get(this.srcObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      this.tryConvValue(setValue,this.dstField,this.srcField) match {
        case Failure(exception) => {
          System.err.println(exception.toString());
          return;
        }
        case Success(value) => { setValue = value }
      }
      dstTypeInfo.setValue(dstObject,this.item.dstKey,setValue)
      if(dstObject.isInstanceOf[INotifyPropertyChanged]) {
          dstObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.dstKey)
      }
  }

  def tryConvValue(setValue:Any,toField:FieldInfo,fromField:FieldInfo):Try[Any] = {
     if(setValue == null) return Success(setValue)
      val setValueTypName = setValue.getClass().getName();
      if(setValueTypName != toField.Name) {
        if(setValue.isInstanceOf[scala.collection.IndexedSeq[Any]] && toField.typName == "scala.collection.IndexedSeq[scala.Any]") {
           return Success(setValue);
        }
        DynTypeConv.convertStrTypeTry(setValueTypName,toField.typName,setValue)
      } else {
        Success(setValue)
      }
  }

  def setDst2Src():Unit = {
      var setValue = this.dstField.get(this.dstObject);
      if(this.item.conv.isDefined) {
        setValue = this.item.conv.get.conv(setValue)
      }
      srcTypeInfo.setValue(srcObject,this.item.sourceKey,setValue)
      if(srcObject.isInstanceOf[INotifyPropertyChanged]) {
          srcObject.asInstanceOf[INotifyPropertyChanged].callPropertyChanged(item.sourceKey)
      }
  }

  def release(): Unit = {
    item.typ match
      case BindingType.Src2Dst => {
        val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
        dstObject match
          case dstNotityObject:INotifyPropertyChanged => {
            srcNotity.unLinkNotifyObject(dstNotityObject,srcField,dstField)
          }
          case _ => srcNotity.removeHandle(onSrcPropertyChanged)
      }
      case BindingType.Dst2Src => {
        val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
        srcObject match
          case srcNotity:INotifyPropertyChanged => {
            dstNotity.unLinkNotifyObject(srcNotity,dstField,srcField)
          }
          case _ => dstNotity.removeHandle(onDstPropertyChanged)       
      }
      case BindingType.Both => {
         val srcNotity = srcObject.asInstanceOf[INotifyPropertyChanged];
         val dstNotity = dstObject.asInstanceOf[INotifyPropertyChanged];
         srcNotity.unLinkNotifyObject(dstNotity,srcField,dstField)
         dstNotity.unLinkNotifyObject(srcNotity,dstField,srcField)
      }
  }
}

object BindingInst {
  def create(src:Any,dst:Any,item:BindingItem):Try[BindingInst] = Try {
    val srcTypeInfo = Assembly.getTypeInfoOrThrow(src);
    val dstTypeInfo = Assembly.getTypeInfoOrThrow(dst);
    //println(s"bindingInst ${src} to ${dst}");
    val srcField:FieldInfo = srcTypeInfo.getFieldTry(item.sourceKey).get;
    val dstField:FieldInfo = dstTypeInfo.getFieldTry(item.dstKey).get;
    BindingInst(item,src,dst,srcTypeInfo,dstTypeInfo,srcField,dstField)
  }
}