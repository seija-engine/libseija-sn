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
import scala.collection.mutable.HashMap;
import ui.resources.UIResourceMgr
import scala.collection.mutable.ArrayBuffer
import ui.visualState.VisualStateGroupList
import ui.visualState.VisualStateGroup
import core.reflect.Assembly
import ui.ElementNameScope
import ui.xml.IXmlObject


class UIElement extends INotifyPropertyChanged with Cloneable with IXmlObject derives ReflectType {
    protected var entity:Option[Entity] = None
    protected var style:Option[Style] = None
    protected var _dataContext:Any = null;
    protected var isEntered:Boolean = false;
    var templateParent:Option[UIElement] = None;

    var Name:String = null;
    protected var _hor:LayoutAlignment = LayoutAlignment.Stretch
    protected var _ver:LayoutAlignment = LayoutAlignment.Stretch
    protected var _width:SizeValue = SizeValue.Auto
    protected var _height:SizeValue = SizeValue.Auto
    protected var _padding:Thickness = Thickness.zero
    protected var _margin:Thickness = Thickness.zero

    protected var bindItemList:ListBuffer[BindingItem] = ListBuffer.empty
    protected var bindingInstList:ListBuffer[BindingInst] = ListBuffer.empty
    protected var parent:Option[UIElement] = None;
    var children:ListBuffer[UIElement] = ListBuffer.empty

    protected var resources:UIResource = UIResource.empty();

    protected var curViewStateDict:HashMap[String,String] = HashMap.empty;
    var visualStateGroups:VisualStateGroupList = VisualStateGroupList();

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
    def setStyle(style:Option[Style]):Unit = {
        this.style = style;
    }
    def dataContext = this._dataContext;
    def dataContext_=(value:Any) = {
        this._dataContext = value;
        this.callPropertyChanged("dataContext",this);
        this.onDataContextChanged();
        this.children.foreach { child => {
          if(child.dataContext == null) {
            child.onDataContextChanged()
          }
       }};
    }

    def Awake():Unit = {
        this.children.foreach(child => {
            child.setParent(Some(this));
            child.Awake();
        });
    }

    def getEntity():Option[Entity] = this.entity;

    def addChild(elem:UIElement) = {
       elem.parent = Some(this);
       this.children.addOne(elem);
    }

    def insertChild(index:Int,elem:UIElement):Unit = {
        elem.parent = Some(this);
        this.children.insert(index,elem);
    }

    def setParent(elem:Option[UIElement]) = {
        this.parent = elem;
    }

    def Enter():Unit = {
        this.applyStyle();
        this.applyBindItems();
        this.OnEnter();
        this.isEntered = true;
        this.children.foreach(child => {
          child.setParent(Some(this));
          child.Enter()
        });
    }

    def OnEnter(): Unit = { this.createBaseEntity(true); }

    def OnAddContent(value:Any):Unit = { }

    protected def onDataContextChanged():Unit = {
       if(!this.isEntered) return;
       for(curItem <- this.bindItemList) {
          if(curItem.sourceType == BindingSource.Data) {
            val oldInstIndex = this.bindingInstList.indexWhere(_.item == curItem);
            if(oldInstIndex >= 0) {
              val inst = this.bindingInstList.remove(oldInstIndex);
              DataBindingManager.removeInst(inst);
            }
            val findDataContext = this.findDataContext();
            if(findDataContext != null) {
                DataBindingManager.binding(findDataContext,this,curItem).logError() match {
                    case Success(Some(inst)) => this.bindingInstList += inst;
                    case _ => {}
                }
            }
          }
       }
    }

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
                case BindingSource.Owner => {
                    if(this.templateParent.isDefined) {
                      DataBindingManager.binding(this.templateParent.get,this,bindItem).logError() match {
                        case Success(Some(inst)) => {
                            this.bindingInstList += inst;
                        } 
                        case _=> {}
                      }
                    }
                }
                case BindingSource.Data => {
                    val findDataContext = this.findDataContext();
                    if(findDataContext != null) {
                        DataBindingManager.binding(findDataContext,this,bindItem).logError() match {
                            case Success(Some(inst)) => this.bindingInstList += inst;
                            case _ => {}
                        }
                    }
                }
            
       }
    }

    def findDataContext():Any = {
        if(this._dataContext == null) {
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

     def findResDataTemplate(dataType:String):Option[DataTemplate] = {
        val dataTemplate = this.resources.findDataTemplate(dataType);
        if(dataTemplate.isDefined) {
            return dataTemplate;
        }
        this.parent.flatMap(parent => parent.findResDataTemplate(dataType))
    }

    def findDataTemplate(dataType:String):Option[DataTemplate] = {
        val findDataTemplate = this.findResDataTemplate(dataType);
        if(findDataTemplate.isDefined) {
            return findDataTemplate;
        }
        UIResourceMgr.appResource.findDataTemplate(dataType)
    }
    
    def setViewState(groupName:String,stateName:String):Unit = {
        if(!this.curViewStateDict.contains(groupName)) {
            this.curViewStateDict.put(groupName,stateName);
        } else {
            this.curViewStateDict.update(groupName,stateName);
        }
        this.onViewStateChanged(groupName,stateName);
    }

    protected def onViewStateChanged(changeGroup:String,newState:String):Unit = {
        val visualGroup = this.visualStateGroups.getGroup(changeGroup);
        if(visualGroup.isEmpty) return;
        this.applyVisualGroup(visualGroup.get,newState,None);
    }

    protected def applyVisualGroup(group:VisualStateGroup,newState:String,nameScope:Option[ElementNameScope]):Unit = {
       val newVisualState = group.getState(newState);
       if(newVisualState.isEmpty) return;
       val typeInfo = Assembly.getTypeInfo(this);
       if(typeInfo.isDefined) {
         for(setter <- newVisualState.get.Setters.setters) {
           if(setter.target == null) {
              typeInfo.get.getField(setter.key).foreach {f => 
                f.set(this,setter.value);
                this.callPropertyChanged(setter.key,this);
              };
           } else nameScope.foreach { scope =>
              val findElement = scope.getScopeElement(setter.target);
              findElement.foreach { elem =>
                Assembly.getTypeInfo(elem).get.getField(setter.key).foreach {f => 
                  f.set(elem,setter.value);
                  elem.callPropertyChanged(setter.key,this);
                }
              };
           }
         }
       }
    }

    def Exit():Unit = {
        this.children.foreach(_.Exit());
        this.bindingInstList.foreach(DataBindingManager.removeInst);
        this.bindingInstList.clear();
    }

    override def clone():UIElement = {
        val cloneObject = super.clone().asInstanceOf[UIElement];
        cloneObject.children = new ListBuffer[UIElement]();
        cloneObject.visualStateGroups = this.visualStateGroups.clone();
        for(child <- this.children) {
            val cloneChild = child.clone();
            cloneObject.children += cloneChild;
        }
        cloneObject
    }

    def Release():Unit = {
        this.Exit();
        this.children.foreach(_.Release());
        this.entity.foreach(_.destroy());
    }
}

object UIElement {
    val zero:UIElement = UIElement()
}