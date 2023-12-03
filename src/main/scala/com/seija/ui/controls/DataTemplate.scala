package com.seija.ui.controls
import com.seija.ui.controls.BaseTemplate
import com.seija.core.reflect.*;
import com.seija.ui.ContentProperty
import com.seija.ui.resources.BaseUIResource;
import scala.util.Try
import scala.util.Success
import com.seija.ui.ElementNameScope
import com.seija.sxml.vm.VMValue

@ContentProperty("content")
class DataTemplate extends BaseTemplate with BaseUIResource derives ReflectType {
    var key:String = ""
    var dataType:String = "";
    def getKey: String = this.key;
    
    var content:UIElement = UIElement.zero;

    override def LoadContent(parent: UIElement,scope:Option[ElementNameScope]): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        this.setUIElementTemplate(instObject,parent,None);
        Success(instObject)
    }
}
