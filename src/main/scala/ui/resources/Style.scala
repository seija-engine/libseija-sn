package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Growable
import ui.xml.XmlNSResolver
import core.logError;

@ContentProperty("setterList")
class Style extends BaseUIResource derives ReflectType {
    var forType:String = "";
    var isGetTypeFail:Boolean = false;
    var forTypeInfo:Option[TypeInfo] = None;
    var setterList:SetterGroup = SetterGroup(this)

    def getForTypeInfo():Option[TypeInfo] = {
        if(isGetTypeFail) return None;
        if(forTypeInfo.isDefined) { return forTypeInfo; }
        val retType = XmlNSResolver.default.resolver(forType).flatMap(Assembly.get);
        if(retType.isEmpty) { this.isGetTypeFail = true; }
        this.forTypeInfo = retType;
        retType
    }
}


case class SetterGroup(style:Style) extends Growable[Setter] {
   var setterList:ArrayBuffer[Setter] = ArrayBuffer.empty;
   override def addOne(setter: Setter): this.type = {
     val styleTypeInfo = style.getForTypeInfo();
     val field = styleTypeInfo.flatMap(_.getField(setter.key));
     if(field.isDefined) {
       val fromTypName = setter.value.getClass().getName();
       val tryConvValue = DynTypeConv.convertStrTypeTry(setter.value.getClass().getName(),field.get.typName,setter.value);
       tryConvValue.logError();
       if(tryConvValue.isSuccess) {
          setter.value = tryConvValue.get;
          this.setterList.addOne(setter);
       }
     }
     this
   }

   override def clear(): Unit = { this.setterList.clear(); }
}
