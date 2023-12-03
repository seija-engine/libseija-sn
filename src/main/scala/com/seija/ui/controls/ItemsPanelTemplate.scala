package com.seija.ui.controls
import com.seija.ui.IPostReader
import com.seija.ui.resources.BaseUIResource
import com.seija.ui.ContentProperty
import com.seija.core.reflect.ReflectType
import com.seija.ui.ElementNameScope
import scala.util.Try
import scala.util.Success

@ContentProperty("content")
class ItemsPanelTemplate extends BaseTemplate with IPostReader with BaseUIResource derives ReflectType {
    var key:String = "";
    def getKey: String = this.key;
    var content:UIElement = UIElement.zero

    override def OnPostRead():Unit = { }
    
    
    override def LoadContent(parent: UIElement, nameScope: Option[ElementNameScope]): Try[UIElement] = {
        val instObject:UIElement = content.clone();
        setUIElementTemplate(instObject,parent,nameScope);
        instObject match
            case panel:Panel => panel.isItemsHost = true;
            case _ =>
        Success(instObject)
    }
}