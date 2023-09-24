package ui.visualState

import scala.collection.mutable.HashMap as MutHashMap
import scala.collection.immutable.HashMap;
import ui.resources.Style
import ui.xml.UISXmlEnv
import core.reflect.TypeInfo
import core.logError
import scala.collection.mutable.ArrayBuffer
import ui.resources.Setter
import ui.ElementNameScope
import ui.controls.UIElement
import core.reflect.Assembly

case class DictSetter(stateSetterDict:HashMap[String,Any]) extends VisualStateChangedHandle {
    val statesSetter:MutHashMap[String,ArrayBuffer[Setter]] = MutHashMap.empty
    
    init()

    def init():Unit = {
        val typeInfo = UISXmlEnv.getGlobal("*type-info*").get.asInstanceOf[TypeInfo]
        for((stateName,stateValue) <- stateSetterDict) {
            stateValue match
                case setDict:HashMap[?,?] => {
                    val strSetterDict = setDict.asInstanceOf[HashMap[String,Any]]
                    val setterList = Style.readSetterList(strSetterDict,typeInfo)
                    setterList.logError()
                    if(setterList.isSuccess) {
                        this.statesSetter.put(stateName,setterList.get)
                    }
                }
        }
    }

    override def applyNameScope(nameScope: ElementNameScope): Unit = {
        statesSetter.values.foreach(_.foreach(setter => setter.applyNameScope(nameScope)))
    }

    override def onViewStateChanged(element:UIElement,changeGroup:String,newState:String,nameScope:Option[ElementNameScope]):Unit = {
        val typeInfo = Assembly.getTypeInfo(element);
        if(typeInfo.isEmpty) return;
        val setterList = this.statesSetter.get(newState)
        if(setterList.isEmpty) return;
        for(setter <- setterList.get) {
          if(setter.target == null) {
            typeInfo.get.getField(setter.key).foreach {f => 
                f.set(element,setter.value);
                element.callPropertyChanged(setter.key,this);
            }
          } else nameScope.foreach {scope =>
            val findElement = scope.getScopeElement(setter.target)
            findElement.foreach { elem =>

                Assembly.getTypeInfo(elem).get.getField(setter.key).foreach {f => 
                  f.set(elem,setter.value);
                  elem.callPropertyChanged(setter.key,this);
                }
            }
          }
        }
    }

    override def OnPostReadResource():Unit = {
      this.statesSetter.values.foreach(_.foreach(_.OnPostReadResource()))
    }
}

 /*
    protected def applyVisualGroup(group:VisualStateGroup,newState:String,nameScope:Option[ElementNameScope]):Unit = {
       val newVisualState = group.getState(newState);
       if(newVisualState.isEmpty) return;
       val typeInfo = Assembly.getTypeInfo(this);
       if(typeInfo.isDefined) {
         for(setter <- newVisualState.get.Setters.setters) {
           if(setter.target == null) {
              typeInfo.get.getField(setter.key).foreach {f => 
                f.set(this,setter.value);
                this.callPropertyChanged(setter.key,this);
              };
           } else nameScope.foreach { scope =>
              val findElement = scope.getScopeElement(setter.target);
              findElement.foreach { elem =>
                Assembly.getTypeInfo(elem).get.getField(setter.key).foreach {f => 
                  f.set(elem,setter.value);
                  elem.callPropertyChanged(setter.key,this);
                }
              };
           }
         }
       }
    }*/