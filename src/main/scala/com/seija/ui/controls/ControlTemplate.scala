package com.seija.ui.controls
import com.seija.ui.ElementNameScope;
import com.seija.ui.controls.BaseTemplate
import com.seija.core.reflect.*;
import com.seija.ui.ContentProperty
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.collection.mutable.HashMap;
import scala.collection.mutable;
import com.seija.core.ICopy;
import com.seija.core.copyObject
import scala.util.Success
import com.seija.ui.IPostReader
import com.seija.ui.resources.BaseUIResource
import com.seija.ui.visualState.VisualStateList

@ContentProperty("content")
class ControlTemplate extends BaseTemplate with ElementNameScope with IPostReader with BaseUIResource derives ReflectType {
    var key:String = "";
    var forType:String = null;
    def getKey: String = this.key;
    var nameDict:HashMap[String,UIElement] = HashMap.empty;
    var content:UIElement = UIElement.zero;
    var vsm:VisualStateList = VisualStateList()
   
    override def OnPostRead():Unit = {
        this.putNameToScope(content)
        this.vsm.applyNameScope(this)
    }

    protected def putNameToScope(element:UIElement):Unit = {
        if(element.Name != null && element.Name != "") {
            this.nameDict.put(element.Name,element);
        }
        element.children.foreach(putNameToScope)
    }

    override def getScopeElement(name:String):Option[UIElement] = { this.nameDict.get(name) }

    override def LoadContent(parent:UIElement,nameScope:Option[ElementNameScope]): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        setUIElementTemplate(instObject,parent,nameScope);
        //nameScope.foreach{v =>
        //    if(v.isInstanceOf[Control]) {
        //       println(s"${nameScope} = ${v.asInstanceOf[Control].nameDict}") 
        //    }    
        //}
        Success(instObject)
    }

}