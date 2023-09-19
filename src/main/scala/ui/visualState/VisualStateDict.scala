package ui.visualState
import ui.ContentProperty
import core.reflect.*
import scala.collection.immutable.HashMap
import scala.collection.mutable.HashMap as MutHashMap
import sxml.vm.ClosureData
import ui.xml.IXmlObject
import ui.xml.UISXmlEnv
import ui.resources.Style
import core.logError
import scala.util.Failure
import scala.util.Success
import scala.collection.mutable.ArrayBuffer
import ui.resources.Setter
import ui.ElementNameScope
import ui.IPostReader

@ContentProperty("content")
class VisualStateDict extends IXmlObject  derives ReflectType {
    var content:HashMap[String,ClosureData] = HashMap()
    var stateDict:MutHashMap[String,ArrayBuffer[Setter]] = MutHashMap.empty

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
                val setterList = Style.readSetterList(strSetterDict,typeInfo.get.asInstanceOf[TypeInfo])
                setterList match
                  case Failure(exception) => System.err.println(exception.toString())
                  case Success(value) => {
                    this.stateDict.put(stateName,value)
                  }
                
              }
            }
            case _ => 
       }
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
      for(setterList <- stateDict.values) {
        for(setter <- setterList) {
          setter.applyNameScope(nameScope)
        }
      }
    }
}