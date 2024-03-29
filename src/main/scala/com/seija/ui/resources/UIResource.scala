package com.seija.ui.resources
import com.seija.core.reflect.*;
import com.seija.ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Await
import scala.collection.mutable.{Buffer,Growable,HashMap}
import scala.collection.mutable.Growable
import com.seija.core.reflect.*;
import com.seija.ui.controls.DataTemplate
import com.seija.ui.xml.IXmlObject
import com.seija.ui.controls.ControlTemplate

trait IPostReadResource {
    def OnPostReadResource():Unit
}

@ContentProperty("resList")
class UIResource extends Growable[BaseUIResource] derives ReflectType {
    var resList:ArrayBuffer[BaseUIResource] = ArrayBuffer[BaseUIResource]()
    protected var styleDict:HashMap[String,Style] = HashMap.empty
    protected var dataTemplateDict:HashMap[String,DataTemplate] = HashMap.empty
    protected var controlTemplateDict:HashMap[String,ControlTemplate] = HashMap.empty
    protected var allResDict:HashMap[String,BaseUIResource] = HashMap.empty

    def findStyle(key:String):Option[Style] = {
        this.styleDict.get(key)
    }

    def findDataTemplate(key:String):Option[DataTemplate] = {
        this.dataTemplateDict.get(key)
    }

    def findRes(resName:String):Option[BaseUIResource] = {
        this.allResDict.get(resName)
    }

    override def clear(): Unit = this.resList.clear();

    override def addOne(elem: BaseUIResource): this.type = {
        this.resList += elem
        elem match
            case style:Style => {
                if(style.getKey == "") {
                    val autoKey = style.forTypeInfo.name
                    this.styleDict.put(autoKey,style)
                    this.allResDict.put(autoKey,style)
                } else {
                    this.styleDict.put(style.getKey,style)
                    this.allResDict.put(style.getKey,style)
                }
            }
            case dataTemplate: DataTemplate => {
                this.dataTemplateDict.addOne(dataTemplate.dataType,dataTemplate);
                this.allResDict.put(dataTemplate.dataType,dataTemplate);
            }
            case controlTemplate:ControlTemplate => {
                if(controlTemplate.key != null) { 
                    this.controlTemplateDict.put(controlTemplate.key,controlTemplate);
                    this.allResDict.put(controlTemplate.key,controlTemplate);
                }
            }
            case _ =>
        this
    }
}

object UIResource {
    def empty():UIResource = { new UIResource(); }
}

trait BaseUIResource {
   def getKey:String
}
