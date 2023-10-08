package ui.controls
import core.reflect.*;
import scala.collection.mutable.ListBuffer
import ui.xml.XmlUIElement
import core.logError;
import ui.ContentProperty
import ui.core.Canvas;
import ui.xml.IXmlObject

@ContentProperty("children")
class Panel extends UIElement with IXmlObject derives ReflectType {
    var isClip:Boolean = false;
    var isCanvas:Boolean = false;

    var _isItemsHost:Boolean = false;
    def isItemsHost:Boolean = this._isItemsHost
    def isItemsHost_=(value:Boolean):Unit = {
      this._isItemsHost = value;callPropertyChanged("isItemsHost",this)
    }

    private var _itemGenerator:ItemContainerGenerator = null
    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      this.checkAddCanvas();
      //println(s"Panel OnEnter ${this.getEntity()} ${this.parent}")
      this.handleItemsHost()
    }

    def handleItemsHost():Unit = {
      if(this.isItemsHost) {
       this.connectToGenerator()
       this.generateChildren()
      }
    }

    def connectToGenerator():Unit = {
      val itemsControl = ItemsControl.GetItemsOwner(this)
      itemsControl match
        case Some(value) => {
          _itemGenerator = value.itemGenerator
        }
        case _ => slog.error("Panel ItemsHost not found ItemsControl")
    }

    def generateChildren():Unit = {
      if(_itemGenerator != null) {
        
      }
    }

    def checkAddCanvas():Unit = {
      if(this.isCanvas || this.isClip) {
         val curEntity = this.getEntity().get;
         curEntity.add[Canvas](builder => {
            builder.isClip = this.isClip;
         });
      }
    }

    override def OnAddContent(value: Any): Unit = {
      value match
        case elemValue: UIElement =>
          elemValue.setParent(Some(this))
          elemValue.setLogicParent(Some(this))
        case _ =>
    }
}