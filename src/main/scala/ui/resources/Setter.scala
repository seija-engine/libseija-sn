package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import core.logError;
import ui.IAwake
import ui.ElementNameScope

@ContentProperty("value")
class Setter extends IApplyStyleType derives ReflectType {
  var key: String = "";
  var value: Any = null;
  var target:String = null;

  private var typInfo: Option[TypeInfo] = None;

  override def applyType(typInfo: Option[TypeInfo]): Unit = {
    if(this.target != null) return;
    val info = typInfo.flatMap(_.getField(this.key));
    this.setValue(info);
  }

  protected  def setValue(info:Option[FieldInfo]):Unit = {
    if (info.isDefined) {
      val fromTypName = this.value.getClass().getName();
      val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,info.get.typName,this.value);
      tryConvValue.logError();
      if (tryConvValue.isSuccess) {
        this.value = tryConvValue.get;
        this.callValueTraits();
      }
    } else {
      System.err.println(s"not found field ${this.key} in ${typInfo.map(_.name)}");
    }
  }

  def applyNameScope(nameScope:ElementNameScope):Unit = {
      if(this.target == null) return;
      val targetElement = nameScope.getScopeElement(this.target);
      targetElement match {
        case Some(value) => {
           val info = Assembly.getTypeInfo(value).flatMap(_.getField(this.key));
           this.setValue(info);
        }
        case None => System.err.println(s"not found name in setter ${this.target} key:${this.key}");
      }   
  }

  protected def callValueTraits(): Unit = {
    if (this.value != null) {
      var callValue = this.value;
      if (this.value.isInstanceOf[Some[Any]]) {
        callValue = this.value.asInstanceOf[Some[Any]].get;
      }
      if(callValue.isInstanceOf[IAwake]) {
        callValue.asInstanceOf[IAwake].Awake();
      }
      if (callValue.isInstanceOf[IApplyStyleType]) {
        callValue.asInstanceOf[IApplyStyleType].applyType(typInfo);
      }
    }
  }

}
