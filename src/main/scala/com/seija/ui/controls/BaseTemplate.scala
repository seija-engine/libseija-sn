package com.seija.ui.controls

import com.seija.ui.controls.UIElement
import scala.util.Try
import scala.util.Failure
import com.seija.ui.ElementNameScope

class BaseTemplate extends Cloneable {
    def LoadContent(parent:UIElement,nameScope:Option[ElementNameScope]):Try[UIElement] = { Failure(NotImplementedError()) }

    protected def setUIElementTemplate(curElement:UIElement,templateParent:UIElement,nameScope:Option[ElementNameScope]):Unit = {
        curElement.templateParent = Some(templateParent);
        nameScope.foreach {scope => 
          if(curElement.Name != null) {
             scope.setScopeElement(curElement.Name,curElement);  
          }
        };
        for(child <- curElement.children) {
            setUIElementTemplate(child,templateParent,nameScope);
        }
    }
}