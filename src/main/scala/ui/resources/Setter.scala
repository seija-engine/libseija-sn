package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import core.logError;

@ContentProperty("value")
class Setter extends IApplyStyleType derives ReflectType {
    var key:String = "";
    var value:Any = null;

    private var typInfo:Option[TypeInfo] = None;

    override def applyType(typInfo: Option[TypeInfo]): Unit = {
        val info = typInfo.flatMap(_.getField(this.key));
        if(info.isDefined) {
            val fromTypName = this.value.getClass().getName();
            val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,info.get.typName,this.value);
            tryConvValue.logError();
            if(tryConvValue.isSuccess) {
                this.value = tryConvValue.get;
                println(s"set realvalue ${this.key} = ${this.value}");
                if(this.value != null && this.value.isInstanceOf[IApplyStyleType]) {
                    this.value.asInstanceOf[IApplyStyleType].applyType(typInfo);
                }
            }
        } else {
            System.err.println(s"not found field ${this.key} in ${typInfo.map(_.name)}");
        }
    }


}