package ui.visualState
import ui.ContentProperty
import core.reflect.*
import scala.collection.immutable.HashMap
import sxml.vm.ClosureData
import ui.xml.IXmlObject
import ui.xml.UISXmlEnv
import ui.resources.Style
import core.logError

@ContentProperty("content")
class VisualStateDict extends IXmlObject derives ReflectType {
    var content:HashMap[String,ClosureData] = HashMap()

    override def OnAddContent(value: Any): Unit = {
       val typeInfo = UISXmlEnv.getGlobal("*type-info*");
       if(typeInfo.isEmpty) return

       val dict = value.asInstanceOf[HashMap[String,Any]]
       for(kv <- dict) {
        kv._2 match
            case stateDict:HashMap[?, ?] => {
              val strStateDict = stateDict.asInstanceOf[HashMap[String,Any]]
              for((stateName,setDict) <- strStateDict) {
                val strSetterDict = setDict.asInstanceOf[HashMap[String,Any]]
                println(strSetterDict)
                val setterList = Style.readSetterList(strSetterDict,typeInfo.get.asInstanceOf[TypeInfo])
                println(setterList)
                //println(setterList)
              }
            }
            case _ => 
        
       }
    }
}