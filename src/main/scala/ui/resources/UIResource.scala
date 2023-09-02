package ui.resources
import core.reflect.*;
import ui.ContentProperty;
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Await
import scala.collection.mutable.{Buffer,Growable,HashMap}
import scala.collection.mutable.Growable
import core.reflect.*;
import ui.controls.DataTemplate
import ui.xml.IXmlObject
import ui.controls.ControlTemplate

@ContentProperty("resList")
class UIResource extends Growable[BaseUIResource] with IXmlObject derives ReflectType {
    var resList:ArrayBuffer[BaseUIResource] = ArrayBuffer[BaseUIResource]()
    protected var styleDict:HashMap[String,OldStyle] = HashMap.empty
    protected var dataTemplateDict:HashMap[String,DataTemplate] = HashMap.empty
    protected var controlTemplateDict:HashMap[String,ControlTemplate] = HashMap.empty
    protected var allResDict:HashMap[String,BaseUIResource] = HashMap.empty

    def findStyle(key:String):Option[OldStyle] = {
        this.styleDict.get(key)
    }

    def findDataTemplate(key:String):Option[DataTemplate] = {
        this.dataTemplateDict.get(key)
    }

    def findRes(resName:String):Option[BaseUIResource] = {
        this.allResDict.get(resName)
    }

    override def clear(): Unit = this.resList.clear();

    override def OnAddContent(value: Any): Unit = {
        UIResourceMgr.appResource.addOne(value.asInstanceOf[BaseUIResource]);
    }

    override def addOne(elem: BaseUIResource): this.type = {
        this.resList.addOne(elem)
        elem match {
            case style:OldStyle => {
               if(style.getKey == "") {
                 val autoKey = style.forTypeInfo.map(_.name).getOrElse("");
                 this.styleDict.put(autoKey,style);
                 this.allResDict.put(autoKey,style);
               } else {
                 println(s"add Style Key:${style.getKey}");
                 this.styleDict.put(style.getKey,style)
                 this.allResDict.put(style.getKey,style);
               }
               
            }
            case dataTemplate:DataTemplate => {
                this.dataTemplateDict.addOne(dataTemplate.dataType,dataTemplate);
                this.allResDict.put(dataTemplate.dataType,dataTemplate);
            }
            case controlTemplate: ControlTemplate => {
                if(controlTemplate.key != null) { 
                    this.controlTemplateDict.put(controlTemplate.key,controlTemplate);
                    this.allResDict.put(controlTemplate.key,controlTemplate);
                }
            }
        }
        this
    }
}

object UIResource {
    def empty():UIResource = { new UIResource(); }
}

trait BaseUIResource {
   def getKey:String
}
