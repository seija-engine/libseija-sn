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
import ui.controls.UIElement
import ui.resources.IPostReadResource

trait VisualStateChangedHandle extends IPostReadResource {
   def applyNameScope(nameScope:ElementNameScope):Unit
   def onViewStateChanged(element:UIElement,changeGroup:String,newState:String,nameScope:Option[ElementNameScope]):Unit;
}

enum StateMatch {
   case Single(name:String)
   case Any
}

case class StateChangedHandle(sMatch:StateMatch,handle:VisualStateChangedHandle)

@ContentProperty("content")
class VisualStateList extends IXmlObject with IPostReadResource derives ReflectType {
    var content:Vector[Any] = Vector()
    var forType:String = null;
    
    private val handleList:ArrayBuffer[StateChangedHandle] = ArrayBuffer()

    override def OnAddContent(value: Any): Unit = {
       for(idx <- 0.until(this.content.length,2)) {
         val matchKey = this.content(idx)
         val stateMatch = matchKey match
          case strKey:String if(strKey == "*") => Some(StateMatch.Any)
          case strKey:String => Some(StateMatch.Single(strKey))
          case _             => None
         val matchValue = this.content(idx + 1)
         val changedSetter:Option[VisualStateChangedHandle] = matchValue match
          case dictValue:HashMap[?,?] => Some(DictSetter(dictValue.asInstanceOf[HashMap[String,Any]]))
          case closure:ClosureData => Some(ClosureSetter(closure))
          case _ => { slog.error(s"not support setter type:${matchValue}"); None  }
         if(stateMatch.isDefined && changedSetter.isDefined) {
            handleList +=  StateChangedHandle(stateMatch.get,changedSetter.get)
         }
       }
    }

    def applyNameScope(nameScope:ElementNameScope):Unit = {
      this.handleList.foreach(_.handle.applyNameScope(nameScope))
    }

    def onViewStateChanged(element:UIElement,changeGroup:String,newState:String,nameScope:Option[ElementNameScope]):Unit = {
      for(handleItem <- this.handleList) {
         handleItem.sMatch match
            case StateMatch.Single(name) => {
               if(name == changeGroup) handleItem.handle.onViewStateChanged(element,changeGroup,newState,nameScope)
            }
            case ui.visualState.StateMatch.Any => handleItem.handle.onViewStateChanged(element,changeGroup,newState,nameScope)
      }
    }

    override def clone():VisualStateList = {
        super.clone().asInstanceOf[VisualStateList];
    }

    override def OnPostReadResource(): Unit = {
      this.handleList.foreach(_.handle.OnPostReadResource())
    }
}