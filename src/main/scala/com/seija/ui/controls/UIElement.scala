package com.seija.ui.controls
import com.seija.core.Entity
import com.seija.ui.binding.INotifyPropertyChanged
import com.seija.ui.core.{FFISeijaUI, ItemLayout, LayoutAlignment, Rect2D, SizeValue, Thickness,FlexItem}
import com.seija.core.reflect.ReflectType
import scala.Conversion
import com.seija.ui.resources.UIResource
import com.seija.core.logError
import scala.quoted.Expr
import scala.collection.mutable.ListBuffer
import com.seija.transform.Transform
import com.seija.ui.binding.BindingItem
import com.seija.ui.binding.BindingSource
import com.seija.ui.binding.DataBindingManager
import com.seija.ui.binding.BindingInst
import scala.util.Success
import com.seija.core.copyObject
import com.seija.core.ICopy
import com.seija.ui.core.FlexItemComponent
import com.seija.ui.ContentProperty
import scala.collection.mutable.HashMap
import com.seija.ui.resources.UIResourceMgr
import scala.collection.mutable.ArrayBuffer
import com.seija.core.reflect.Assembly
import com.seija.ui.ElementNameScope
import com.seija.ui.event.{IRouteEventElement, RouteEventController,RouteEventArgs}
import com.seija.ui.xml.IXmlObject

import scala.collection.mutable
import com.seija.transform.FFISeijaTransform
import com.seija.ui.resources.Style
import com.seija.ui.visualState.VisualStateList
import com.seija.ui.trigger.TriggerList
import com.seija.ui.core.FlexItemBuilder
import com.seija.ui.core.FlexItem

case class PropertyDefine(propKey:String,default:Any);

class UIElement extends INotifyPropertyChanged
  with Cloneable with IXmlObject with IRouteEventElement derives ReflectType {
    protected var entity:Option[Entity] = None
    protected var style:Option[Style] = None
    protected var _dataContext:Any = null
    protected var isEntered:Boolean = false
    var templateParent:Option[UIElement] = None
    protected var logicParent:Option[UIElement] = None
    var Name:String = "";
    var Id:String = "";
    var flexItem:FlexItemBuilder = null;

    protected var _hor:LayoutAlignment = LayoutAlignment.Stretch
    protected var _ver:LayoutAlignment = LayoutAlignment.Stretch
    protected var _width:SizeValue = SizeValue.Auto
    protected var _height:SizeValue = SizeValue.Auto
    protected var _padding:Thickness = Thickness.zero
    protected var _margin:Thickness = Thickness.zero
    protected var _active:Boolean = true
    protected var _IsEnabled:Boolean = true

    protected var bindItemList:ListBuffer[BindingItem] = ListBuffer.empty
    protected var bindingInstList:ListBuffer[BindingInst] = ListBuffer.empty
    protected var parent:Option[UIElement] = None
    
    var children:ListBuffer[UIElement] = ListBuffer.empty

    protected var resources:UIResource = UIResource.empty();

    protected var curViewStateDict:mutable.HashMap[String,String] = mutable.HashMap.empty
    var vsm:VisualStateList = VisualStateList()
    var triggers:TriggerList = TriggerList()
    private var idScope:Option[IDScope] = None

    //region Setter

    def hor: LayoutAlignment = this._hor;
    def hor_=(value:LayoutAlignment): Unit = { this._hor = value; this.callPropertyChanged("hor") }
    def ver: LayoutAlignment = this._ver;
    def ver_=(value:LayoutAlignment): Unit = { this._ver = value; this.callPropertyChanged("ver") }
    def width: SizeValue = this._width;
    def width_=(value:SizeValue): Unit = { this._width = value; this.callPropertyChanged("width") }
    def height: SizeValue = this._height;
    def height_=(value:SizeValue):Unit = { this._height = value; this.callPropertyChanged("height") }
    def padding:Thickness = this._padding
    def padding_=(value:Thickness):Unit = { this._padding = value; this.callPropertyChanged("padding") }
    def margin = this._margin
    def margin_=(value:Thickness) = { this._margin = value; this.callPropertyChanged("margin") }
    def active = this._active
    def active_=(value:Boolean):Unit = { this._active = value; callPropertyChanged("active")  }
    def IsEnabled:Boolean = this._IsEnabled
    def IsEnabled_=(value:Boolean):Unit = { this._IsEnabled = value;callPropertyChanged("IsEnabled") }

    def setStyle(style:Option[Style]):Unit = {
        this.style = style;
    }
    def dataContext = this._dataContext;
    def dataContext_=(value:Any) = {
        this._dataContext = value;
        this.callPropertyChanged("dataContext");
        this.onDataContextChanged();
    }

    private val exPropDict:HashMap[String,Any] = HashMap.empty
    def GetPropValue(define:PropertyDefine):Any = {
        exPropDict.get(define.propKey).getOrElse(define.default)
    }

    def SetPropValue(define:PropertyDefine,value:Any) = {
        exPropDict.put(define.propKey,value)
        this.onPropertyChanged(define.propKey)
    }
    //endregion

    def Awake():Unit = {
      this.children.foreach(child => {
            child.setParent(Some(this))
            child.Awake()
        })
    }

    def getEntity():Option[Entity] = this.entity

    def addIDScope():Unit = {
        this.idScope = Some(IDScope())
    }

    def findIdScope():Option[IDScope] = {
        var curElement:Option[UIElement] = Some(this);
        while(curElement.isDefined) {
            if(curElement.get.idScope.isDefined) {
                return curElement.get.idScope
            }
            curElement = curElement.get.parent
        }
        None
    }

    def addChild(elem:UIElement):Unit = {
       elem.parent = Some(this);
       this.children.addOne(elem);
    }

    def insertChild(index:Int,elem:UIElement):Unit = {
        elem.parent = Some(this);
        this.children.insert(index,elem);
    }

    def removeChild(elem:UIElement,isRelease:Boolean = true):Unit = {
        val idx = this.children.indexOf(elem)
        if(idx >= 0) {
            this.children.remove(idx)
            if(isRelease) {
                elem.Release()
            }
        }
    }

    def setParent(elem:Option[UIElement]): Unit = { this.parent = elem; }
    def getParent:Option[UIElement] = this.parent
    def setLogicParent(elem:Option[UIElement]):Unit = { 
        this.logicParent = elem
    }
    def getLogicParent:Option[UIElement] = this.logicParent
    def Enter():Unit = {
        if(this.Id != null && this.Id != "") {
            findIdScope().foreach { idScope =>
                idScope.addElement(this.Id,this)
            }
        }
        this.findStyle().foreach {v =>
            this.applyStyle(v,this)    
        }
        this.applyBindItems()
        if(this._dataContext == null) {
            this.dataContext = this.findDataContext()
        }
        this.OnEnter()
        this.onHandleFlexItem()
        this.isEntered = true;
        this.children.foreach(child => {
          child.setParent(Some(this))
          child.Enter()
        });
    }

    def OnEnter(): Unit = { this.createBaseEntity(true); }

    protected def onHandleFlexItem():Unit = {
        if(this.flexItem != null) {
           val curEntity = this.getEntity().get;
           curEntity.add[com.seija.ui.core.FlexItem](builer => {
            builer.shrink = this.flexItem.shrink;
            builer.order = this.flexItem.order;
            builer.grow = this.flexItem.grow;
            builer.basis = this.flexItem.basis;
            builer.alignSelf = this.flexItem.alignSelf;
           })
        }
    }

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

       this.children.foreach { child => {
          if(child.dataContext == null) {
            child.onDataContextChanged()
          }
       }};
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        if(propertyName == "active" && this.isEntered) {
            //println(s"set ACtive:${this.entity.get}=${this._active}");
            this.getEntity().get.setActive(this._active)
        }
        this.triggers.onPropChanged(this,propertyName)
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
        this.addEntityStateInfo(newEntity);
        this.entity = Some(newEntity);
        newEntity
    }

    protected def addEntityStateInfo(entity:Entity):Unit = {
        if(this._active == false) {
            FFISeijaTransform.addStateInfo(entity,this._active)
        }
    }

    def addBindItem(bindItem:BindingItem) = {
        this.bindItemList.addOne(bindItem);
    }
    
    def applyBindItems():Unit = {
       for(bindItem <- this.bindItemList) {
            bindItem.sourceType match {
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
                    //println(s"findDataContext = ${findDataContext}");
                    if(findDataContext != null) {
                        DataBindingManager.binding(findDataContext,this,bindItem).logError() match {
                            case Success(Some(inst)) => this.bindingInstList += inst;
                            case _ => {}
                        }
                    }
                }
                case BindingSource.ID(name) => {
                    this.findIdScope().flatMap(_(name)).foreach {findElement =>
                       DataBindingManager.binding(findElement,this,bindItem).logError()  match {
                            case Success(Some(inst)) => this.bindingInstList += inst;
                            case _ => {}
                        }
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

    def applyStyle(style:Style,element:UIElement) = {
      val typInfo = style.forTypeInfo;
      for(setter <- style.setterList) {
        typInfo.getFieldTry(setter.key).logError().foreach {f => 
            f.set(element,setter.value);    
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
        val findDataTemplate = this.findResDataTemplate(dataType)
        if(findDataTemplate.isDefined) {
            return findDataTemplate
        }
        UIResourceMgr.appResource.findDataTemplate(dataType)
    }
    
    def setViewState(groupName:String,stateName:String):Unit = {
        var isChanged = false;
        if(!this.curViewStateDict.contains(groupName)) {
            this.curViewStateDict.put(groupName,stateName);
            isChanged = true;
        } else {
            if(this.curViewStateDict(groupName) != stateName) {
                this.curViewStateDict.update(groupName,stateName);
                isChanged = true;
            }
        }
        if(isChanged) {
            this.onViewStateChanged(groupName,stateName);
        }
    }

    protected def onViewStateChanged(changeGroup:String,newState:String):Unit = {
        this.vsm.onViewStateChanged(this,changeGroup,newState,None)
    }
   

    private var _routeEventController:RouteEventController = RouteEventController(this)

    def routeEventController: RouteEventController = this._routeEventController
    def setRouteEventElem(elem:IRouteEventElement):Unit = { this._routeEventController = RouteEventController(elem) }
    def getRouteEventParent: Option[IRouteEventElement] = {
      this.getParent
    }

    def Exit():Unit = {
        this.children.foreach(_.Exit());
        this.bindingInstList.foreach(DataBindingManager.removeInst);
        this.bindingInstList.clear();
        if(this.Id != null && this.Id != "") {
            this.idScope.foreach { scope =>
                scope.removeElement(this.Id)
            }
        }
    }

    override def clone():UIElement = {
        val cloneObject = super.clone().asInstanceOf[UIElement];
        cloneObject.children = new ListBuffer[UIElement]()
        cloneObject.handleList = ArrayBuffer.empty
        cloneObject.setRouteEventElem(cloneObject)
        cloneObject.vsm = cloneObject.vsm.clone()
        cloneObject.linkObjectList = ArrayBuffer.empty

        for(child <- this.children) {
            val cloneChild = child.clone()
            cloneObject.addChild(cloneChild)
        }
        cloneObject
    }

    def Release():Unit = {
        this.Exit()
        this.children.foreach(_.Release())
        this.entity.foreach(_.destroy())
    }
}

object UIElement {
    val zero:UIElement = UIElement()
}