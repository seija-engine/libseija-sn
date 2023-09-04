package ui.resources
import core.reflect.TypeInfo
import scala.util.Try
import sxml.vm.VMValue
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.HashMap
import ui.xml.XmlNSResolver
import core.reflect.Assembly
import core.reflect.DynTypeConv
import core.logError;
import ui.xml.SXmlObjectParser
case class Style(
    val forTypeInfo:TypeInfo,
    val setterList:ArrayBuffer[Setter],
    val key:String = ""
) extends BaseUIResource {
    override def getKey:String = this.key
}


case class Setter(
    val key:String,
    val value:Any,
    val target:Any
)



object Style {
  def loadFromValue(attr:VMValue,dict:VMValue):Try[Style] = Try {
    var forType:Option[String] = None
    attr match
           case VMValue.VMString(value) => forType = Some(value) 
           case VMValue.VMMap(value) =>  {
              val attrDict = attr.toScalaValue().asInstanceOf[HashMap[String,Any]]
              forType = attrDict.get("type").map(_.asInstanceOf[String])
           }
           case _ => 
    if(forType.isEmpty) throw new Exception("style need type")
    
    val typInfo = XmlNSResolver.default.resolver(forType.get).flatMap(Assembly.get)
    if(typInfo.isEmpty) throw new Exception(s"not found type ${forType.get}")
    
    val setDict = dict.toScalaValue().asInstanceOf[HashMap[String,Any]]
    val setterList:ArrayBuffer[Setter] = ArrayBuffer()
    for((setName,setValue) <- setDict) {
      var realValue = setValue
      if(setValue.isInstanceOf[sxml.vm.XmlNode]) {
        realValue = SXmlObjectParser(XmlNSResolver.default).parse(setValue.asInstanceOf[sxml.vm.XmlNode]).get
      }
      val field = typInfo.get.getField(setName)
                             .getOrElse(throw new Exception(s"not found field ${setName} in ${typInfo.get.name}"))
      val fromTypName = Assembly.getTypeName(realValue)
      val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,field.typName,realValue)
      tryConvValue.logError()
      if(tryConvValue.isSuccess) {
        setterList += Setter(setName,tryConvValue.get,null)
      }
    }
    Style(typInfo.get,setterList)
  }
}