package ui.visualState
import sxml.vm.ClosureData
import ui.ElementNameScope
import ui.controls.UIElement

case class ClosureSetter(closure:ClosureData) extends VisualStateChangedHandle {
    override def applyNameScope(nameScope: ElementNameScope): Unit = {
        
    }

    override def onViewStateChanged(element:UIElement,changeGroup:String,newState:String,nameScope:Option[ElementNameScope]):Unit = {
        
    }
}