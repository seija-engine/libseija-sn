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
import ui.xml.UISXmlEnv
import ui.ElementNameScope
import ui.xml.ResKey
import core.reflect.FieldInfo
case class Style(
    val forTypeInfo:TypeInfo,
    val setterList:ArrayBuffer[Setter],
    val key:String = ""
) extends BaseUIResource with IPostReadResource {
    override def getKey:String = this.key

    override def OnPostReadResource(): Unit = {
      
      this.setterList.foreach(_.OnPostReadResource())
    }
}


case class Setter(
    var key:String,
    var value:Any,
    val target:String,
    val fieldInfo:FieldInfo = null
) extends IPostReadResource {

  def applyNameScope(nameScope:ElementNameScope):Unit = {
    if(target == null) return;
    val targetElement = nameScope.getScopeElement(this.target)
    targetElement match {
        case Some(value) => { this.setConvValue(value) }
        case None => System.err.println(s"not found name in setter ${this.target} key:${this.key}");
    }
  }

  def setConvValue(value:Any):Unit = {
    val info = Assembly.getTypeInfo(value).flatMap(_.getField(this.key))
    if(info.isDefined) {
      val fromTypName = Assembly.getTypeName(this.value)
      val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,info.get.typName,this.value)
      tryConvValue.logError()
      if (tryConvValue.isSuccess) {
        this.value = tryConvValue.get
      }
    }
  }

  override def OnPostReadResource(): Unit = {
    value match
      case post:IPostReadResource => post.OnPostReadResource()
      case reskey:ResKey => {
        val resName = value.asInstanceOf[ResKey].resName
        UIResourceMgr.appResource.findRes(resName) match
          case None => slog.error(s"not found setter:${key} res key:${resName}")
          case Some(value) => {
            val fromTypName = Assembly.getTypeName(value)
            val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,this.fieldInfo.typName,value)
            tryConvValue.logError()
            if (tryConvValue.isSuccess) {
              this.value = tryConvValue.get
            }
          }
      }
      case _ => 
  }

}



object Style {
  def loadFromValue(attr:VMValue,dict:VMValue):Try[Style] = Try {
    var forType:Option[String] = None
    var strKey:Option[String] = None;
    attr match
           case VMValue.VMString(value) => forType = Some(value) 
           case VMValue.VMMap(value) =>  {
              val attrDict = attr.toScalaValue().asInstanceOf[HashMap[String,Any]]
              forType = attrDict.get("type").map(_.asInstanceOf[String])
              if(attrDict.contains("key")) {
                strKey = Some(attrDict("key").asInstanceOf[String])
              }
           }
           case _ => 
    if(forType.isEmpty) throw new Exception("style need type")
    
    val typInfo = XmlNSResolver.default.resolver(forType.get).flatMap(Assembly.get)
    if(typInfo.isEmpty) throw new Exception(s"not found type ${forType.get}")
    UISXmlEnv.setGlobal("*type-info*",typInfo.get)
    val setDict = dict.toScalaValue().asInstanceOf[HashMap[String,Any]]
    val setterList:ArrayBuffer[Setter] = this.readSetterList(setDict,typInfo.get).get
    UISXmlEnv.setGlobal("*type-info*",null)
   
    Style(typInfo.get,setterList,strKey.getOrElse(""))
  }

  def readSetterList(setDict:HashMap[String,Any],typInfo:TypeInfo):Try[ArrayBuffer[Setter]] = Try {
    val setterList:ArrayBuffer[Setter] = ArrayBuffer()
    for((setName,setValue) <- setDict) {
      var realValue = setValue
      if(realValue != null) {
        if(realValue.isInstanceOf[Setter]) {
          val setter = realValue.asInstanceOf[Setter]
          setter.key = setName
          setterList += setter
        } else {
          if(setValue.isInstanceOf[sxml.vm.XmlNode]) {
            realValue = SXmlObjectParser(XmlNSResolver.default).parse(setValue.asInstanceOf[sxml.vm.XmlNode]).get
          }
          if(!realValue.isInstanceOf[ResKey]) {
            val field = typInfo.getFieldTry(setName).get
            val fromTypName = Assembly.getTypeName(realValue)
            val tryConvValue = DynTypeConv.convertStrTypeTry(fromTypName,field.typName,realValue)
            tryConvValue.logError()
            if(tryConvValue.isSuccess) {
              setterList += Setter(setName,tryConvValue.get,null,field)
            }
          } else {
            val field = typInfo.getFieldTry(setName).get
            setterList += Setter(setName,realValue,null,field)
          }
          
        }
      } else {
        slog.error(s"setter ${setName} value is null")
      }
    }
    setterList
  }
}