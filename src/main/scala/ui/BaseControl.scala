package ui
import _root_.core.Entity;
import java.util.ArrayList
import _root_.core.IFromString
import java.util.ArrayList;
import java.util.HashMap
import _root_.core.reflect.Assembly;
import ui.binding.{BindingItem,INotifyPropertyChanged,BindingSource,DataBindingManager}

class BaseControl extends INotifyPropertyChanged with Cloneable {
    var templateOwner:Option[BaseControl] = None;
    protected var isEntered:Boolean = false;
    protected var entity:Option[Entity] = None;
    var Name: String = "";
    var ClassName: String = "";
    protected var parent:Option[BaseControl] = None;
    protected var childrenList: ArrayList[BaseControl] = new ArrayList[BaseControl]();
    protected var bindItemList:ArrayList[BindingItem] = ArrayList();
    
    protected var _dataContext:Any = null;
    def dataContext = this._dataContext;
    def dataContext_=(value:Any) = {
        this._dataContext = value;
        this.onDataContextChanged();
        this.childrenList.forEach{child => {
          if(child.dataContext == null) {
            child.onDataContextChanged()
          }
        }};
    }

    def setParent(parent:Option[BaseControl]) = this.parent = parent;

    def getEntity():Option[Entity] = this.entity

    def Enter():Unit = {
        if(!this.isEntered) {
          this.isEntered = true;
          this.applyBindItems();
          this.OnEnter();
        }
        this.childrenList.forEach(_.Enter());
    }

     def OnEnter() = {}

    def AddChild(child:BaseControl,isEnter:Boolean = true) = {
        child.setParent(Some(this));
        if(isEnter) {
           child.Enter();
        }
        this.childrenList.add(child);
    }

    override def clone():BaseControl = {
        val cloneObject:BaseControl = super.clone().asInstanceOf[BaseControl]
        cloneObject.childrenList.clear();
        this.childrenList.forEach(v => {
            v.setParent(Some(cloneObject))
            cloneObject.childrenList.add(v.clone())
        });
        cloneObject
    }

    override def onPropertyChanged(propertyName: String): Unit = {
        println(s"onPropertyChanged:${propertyName}")
    }

    def onDataContextChanged():Unit = {
      if(!this.isEntered) return;
      this.updateDataContextBinding();
    }

    def addBindItem(item: BindingItem): Unit = {
        this.bindItemList.add(item);
    }

    def findDataContext():Any = {
        if(this._dataContext == null) {
            if(this.parent.isDefined) {
               return this.parent.get.findDataContext();
            }
        }
        return this._dataContext;
    }

    def updateDataContextBinding():Unit = {
       for(idx <- 0 until this.bindItemList.size()) {
          val curItem = this.bindItemList.get(idx);
          DataBindingManager.removeByDst(this);
          if(curItem.sourceType == BindingSource.Data) {
              this.bindingDataContext(curItem);
          }
       }
    }

    def bindingOwner(item:BindingItem):Unit = {
        if(this.templateOwner.isDefined) {
          DataBindingManager.binding(this.templateOwner.get,Some(item.sourceKey),this,item.dstKey,item.conv);
        }
    }

    def bindingDataContext(item:BindingItem):Unit = {
        val dataCtx = this.findDataContext();
        if(dataCtx != null) {
          DataBindingManager.binding(dataCtx,Some(item.sourceKey),this,item.dstKey,item.conv);
        }
    }

    def applyBindItems():Unit = {
        if(this.bindItemList.size() == 0) return;
        for(idx <- 0 until this.bindItemList.size()) {
          val curItem = this.bindItemList.get(idx);
          curItem.sourceType match
            case BindingSource.Owner => this.bindingOwner(curItem)
            case BindingSource.Data => this.bindingDataContext(curItem)
          
        }
        //TODO Owner 绑定
        /*
        this.updateDataContextBinding();
        for(idx <- 0 until this.bindItemList.size()) {
           val curItem = this.bindItemList.get(idx);
           curItem.sourceType match {
             case BindingSource.Owner => {
                if(this.templateOwner.isDefined) {
                   this.bindObjectList.add(this.templateOwner.get);
                   this.templateOwner.get.addPropertyChangedHandler(this.onBindSourceChanged,curItem);

                   val srcField = Assembly.getTypeInfo(this.templateOwner.get).flatMap(_.GetField(curItem.sourceKey));
                   val srcValue = srcField.map(_.get(this.templateOwner.get));
                   this.onBindSourceChanged(this.templateOwner.get,curItem.sourceKey,srcValue.getOrElse(null),curItem);
                }
             }
             case _ => {}
           }
        }*/
    }

    

    def onBindSourceChanged(src:INotifyPropertyChanged,name:String,newValue:Any,param:Any):Unit = {
       val curItem = param.asInstanceOf[BindingItem];
       val dstField = Assembly.getTypeInfo(this).flatMap(_.GetField(curItem.dstKey));
       if(dstField.isEmpty) return;
       var realValue = newValue;
       if(curItem.conv.isDefined) {
         val conv = curItem.conv.get;
         realValue = conv.conv(newValue);
       }
       dstField.foreach {field => 
          field.set(this,realValue);
       };
    }

   

    def Exit():Unit = {

    }
}