package com.seija.ui.controls
import com.seija.core.reflect.*
import com.seija.ui.ContentProperty
import com.seija.ui.core.Canvas
import com.seija.ui.xml.IXmlObject
import com.seija.ui.binding.CollectionChangedAction
import com.seija
import com.seija.ui.core.EventNode

@ContentProperty("children")
class Panel extends UIElement with IXmlObject derives ReflectType {
    var isClip:Boolean = false;
    var isCanvas:Boolean = false;
    var stopCapture:Boolean = false;
    var stopBubble:Boolean = false;

    var _isItemsHost:Boolean = false;
    def isItemsHost:Boolean = this._isItemsHost
    def isItemsHost_=(value:Boolean):Unit = {
      this._isItemsHost = value;callPropertyChanged("isItemsHost")
    }

    private var _itemGenerator:ItemContainerGenerator = null
    override def OnEnter(): Unit = {
      this.createBaseEntity(true);
      this.checkAddCanvas();
      this.checkEventNode();
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
          _itemGenerator.ItemsChanged = Some(this.OnItemsChanged)
        }
        case _ => slog.error("Panel ItemsHost not found ItemsControl")
    }

    def generateChildren():Unit = {
      if(_itemGenerator != null) {
        this._itemGenerator.StartAt(0)
        var item = this._itemGenerator.GenerateNext()
        while(item.isDefined) {
           this._itemGenerator.PrepareItemContainer(item.get)
           this.addChild(item.get)
           item = this._itemGenerator.GenerateNext()
        }
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

    def checkEventNode():Unit = {
      if(this.stopCapture || this.stopBubble) {
        this.getEntity().get.add[EventNode](builder => {
          builder.stopCapture  = this.stopCapture;
          builder.stopBubble = this.stopBubble;
        })
      }
    }

    override def OnAddContent(value: Any): Unit = {
      value match
        case elemValue: UIElement =>
          elemValue.setParent(Some(this))
          elemValue.setLogicParent(Some(this))
        case _ =>
    }

    def OnItemsChanged(sender:Any,args:ItemsChangedEventArgs):Unit = {
      args.action match
        case CollectionChangedAction.Add => {
          _itemGenerator.StartAt(args.index)
          _itemGenerator.GenerateNext().foreach {element => 
            _itemGenerator.PrepareItemContainer(element)
            this.insertChild(args.index,element)
            element.Enter()
            this.getEntity().get.insertChild(element.getEntity().get,args.index);
          }
        }
        case CollectionChangedAction.Remove => {
          val oldElement = this.children.remove(args.index);
          oldElement.Release();
        }
        case CollectionChangedAction.Replace => {
           this.children(args.index).dataContext = _itemGenerator.host.View.getDataList(args.index);
        }
        case CollectionChangedAction.Move => {
          val dataList = _itemGenerator.host.View.getDataList;
          val a = args.index;
          val b = args.oldIndex;
          val (startIdx,endIdx) = if(a > b) (b,a) else (a,b)
          startIdx.to(endIdx).foreach {index => 
            this.children(index).dataContext = dataList(index);  
          }
        }
        case CollectionChangedAction.Clear => {
          this.children.foreach(_.Release());
          this.children.clear();
        } 
    }

    override def Exit(): Unit = {
      super.Enter()
      if(_itemGenerator != null) {
        _itemGenerator.ItemsChanged = None
      }
    }
}