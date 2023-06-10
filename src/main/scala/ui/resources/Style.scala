package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Growable
import ui.xml.XmlNSResolver
import core.logError;

trait IApplyStyleType {
   def applyType(info:Option[TypeInfo]):Unit;
}

@ContentProperty("setterList")
class Style extends BaseUIResource derives ReflectType {
    var key:String = "";
    var forType:String = "";
    var isGetTypeFail:Boolean = false;
    var forTypeInfo:Option[TypeInfo] = None;
    var setterList:SetterGroup = SetterGroup(this)
    def getKey = this.key;
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
     setter.applyType(styleTypeInfo);
     this.setterList.addOne(setter);
     this
   }

   override def clear(): Unit = { this.setterList.clear(); }
}
