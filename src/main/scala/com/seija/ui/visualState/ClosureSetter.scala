package com.seija.ui.visualState
import com.seija.sxml.vm.ClosureData
import com.seija.ui.ElementNameScope
import com.seija.ui.controls.UIElement

case class ClosureSetter(closure:ClosureData) extends VisualStateChangedHandle {
    override def applyNameScope(nameScope: ElementNameScope): Unit = {
        
    }

    override def onViewStateChanged(element:UIElement,changeGroup:String,newState:String,nameScope:Option[ElementNameScope]):Unit = {
        
    }

    override def OnPostReadResource():Unit = {
      
    }
}