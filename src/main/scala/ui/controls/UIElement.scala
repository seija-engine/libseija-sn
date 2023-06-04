package ui.controls
import core.Entity;
import ui.binding.INotifyPropertyChanged
import ui.core.{LayoutAlignment,SizeValue,Thickness,Rect2D,ItemLayout}
import core.reflect.{autoProps,AutoGetSetter,ReflectType};
import scala.Conversion
import ui.resources.UIResource;
import core.logError;
import scala.quoted.Expr
import core.xml.XmlElement
import scala.collection.mutable.ListBuffer
import transform.Transform
import ui.binding.BindingItem
import ui.binding.BindingSource
import ui.binding.DataBindingManager
import ui.binding.BindingInst
import scala.util.Success
import core.copyObject;
import core.ICopy
import ui.ContentProperty
import ui.resources.Style;
import ui.resources.UIResourceMgr


class UIElement extends INotifyPropertyChanged derives ReflectType {
    protected var entity:Option[Entity] = None
    protected var style:Option[Style] = None
    protected var _dataContext:Option[Any] = None;

    protected var _hor:LayoutAlignment = LayoutAlignment.Stretch
    protected var _ver:LayoutAlignment = LayoutAlignment.Stretch
    protected var _width:SizeValue = SizeValue.Auto
    protected var _height:SizeValue = SizeValue.Auto
    protected var _padding:Thickness = Thickness.zero
    protected var _margin:Thickness = Thickness.zero

    protected var bindItemList:ListBuffer[BindingItem] = ListBuffer.empty
    protected var bindingInstList:ListBuffer[BindingInst] = ListBuffer.empty
    protected var parent:Option[UIElement] = None;
    protected var children:ListBuffer[UIElement] = ListBuffer.empty
    protected var templatedParent:Option[UIElement] = None;

    protected var resources:UIResource = UIResource.empty();

    def hor = this._hor;
    def hor_=(value:LayoutAlignment) = { this._hor = value; this.callPropertyChanged("hor",this) }
    def ver = this._ver;
    def ver_=(value:LayoutAlignment) = { this._ver = value; this.callPropertyChanged("ver",this) }
    def width = this._width;
    def width_=(value:SizeValue) = { this._width = value; this.callPropertyChanged("width",this) }
    def height = this._height;
    def height_=(value:SizeValue) = { this._height = value; this.callPropertyChanged("height",this) }
    def padding = this._padding;
    def padding_=(value:Thickness) = { this._padding = value; this.callPropertyChanged("padding",this) }
    def margin = this._margin;
    def margin_=(value:Thickness) = { this._margin = value; this.callPropertyChanged("margin",this) }
    def dataContext = this._dataContext;
    def dataContext_=(value:Option[Any]) = {
        this._dataContext = value;
        this.callPropertyChanged("dataContext",this);
    }

    def getEntity():Option[Entity] = this.entity;

    def addChild(elem:UIElement) = {
       elem.parent = Some(this);
       this.children.addOne(elem);
    }

    def setParent(elem:Option[UIElement]) = {
        this.parent = elem;
    }

    def Enter():Unit = {
        this.applyStyle();
        this.applyBindItems();
        this.OnEnter();
        
        this.children.foreach(child => {
          child.setParent(Some(this));
          child.Enter()
        });
    }

    def OnEnter(): Unit = { this.createBaseEntity(true); }

    

    protected def createBaseEntity(addBaseLayout:Boolean = true):Entity = {
        val parentEntity = this.parent.flatMap(_.getEntity());
        val newEntity = Entity.spawnEmpty().add[Transform](_.parent = parentEntity).add[Rect2D]()
        if(addBaseLayout) {
            newEntity.add[ItemLayout](v => {
                v.common.hor = this._hor;
                v.common.ver = this._ver;
                v.common.uiSize.width = this._width;
                v.common.uiSize.height = this._height;
                v.common.padding = this._padding;
                v.common.margin = this._margin;
            })
        }
        this.entity = Some(newEntity);
        newEntity
    }

    def addBindItem(bindItem:BindingItem) = {
        println(s"addBindItem: $bindItem")
        this.bindItemList.addOne(bindItem);
    }
    def applyBindItems():Unit = {
       for(bindItem <- this.bindItemList) {
            bindItem.sourceType match
                case BindingSource.Owner => {}
                case BindingSource.Data => {
                    this.findDataContext().foreach{dataContext =>
                        DataBindingManager.binding(dataContext,this,bindItem).logError() match {
                            case Success(Some(inst)) => this.bindingInstList.addOne(inst)
                            case _ => {}
                        }
                    }
                }
            
       }
    }

    def findDataContext():Option[Any] = {
        if(this._dataContext.isEmpty) {
            if(this.parent.isDefined) {
               return this.parent.get.findDataContext();
            }
        }
        return this._dataContext;
    }

    def applyStyle() = {
      val style = this.findStyle();
      if(style.isDefined && style.get.forTypeInfo.isDefined) {
        val typInfo = style.get.forTypeInfo.get;
        for(setter <- style.get.setterList.setterList) {
            typInfo.getFieldTry(setter.key).logError().foreach {f => 
                f.set(this,setter.value);    
            }
        }
      }
    }

    def findStyle():Option[Style] = {
        if(this.style.isDefined) {
           return this.style;
        }
        val styleKey = this.getClass().getName();
        val resStyle = this.findResourceStyle(styleKey)
        if(resStyle.isDefined) {
            return resStyle;
        }
        UIResourceMgr.appResource.findStyle(styleKey)
    }

    def findResourceStyle(key:String):Option[Style] = {
        val style = this.resources.findStyle(key);
        if(style.isDefined) {
           return style;
        }
        this.parent.flatMap(parent => parent.findResourceStyle(key))
    }

    

    def Exit() = {
        this.bindingInstList.foreach(DataBindingManager.removeInst);
        this.bindingInstList.clear();
    }
}

object UIElement {
    val zero:UIElement = UIElement()

    given ICopy[UIElement] with {

    }
}