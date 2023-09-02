package ui.resources
import core.reflect.TypeInfo
import scala.util.Try
import sxml.vm.VMValue
import scala.collection.immutable.HashMap
import ui.xml.XmlNSResolver
import core.reflect.Assembly

case class Style(
    val forTypeInfo:TypeInfo,
    val setterList:Array[Setter],
    val key:String = ""
) extends BaseUIResource {
    override def getKey:String = this.key
}


case class Setter(
    val key:String,
    val value:Any,
    val target:String
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
    for(kv <- setDict) {
        //println(kv)
    }
    Style(typInfo.get,Array())
  }
}