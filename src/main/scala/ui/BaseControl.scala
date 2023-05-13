package ui
import _root_.core.Entity;
import java.util.ArrayList
import _root_.core.IFromString
import java.util.ArrayList;
import java.util.HashMap
import _root_.core.reflect.Assembly;
import ui.binding.{BindingItem,BindingInst,INotifyPropertyChanged,BindingSource,DataBindingManager}
import scala.collection.mutable.ArrayBuffer
import scala.util.Success
import scala.util.Failure

class BaseControl extends INotifyPropertyChanged with Cloneable {
    var templateOwner:Option[BaseControl] = None;
    protected var isEntered:Boolean = false;
    protected var entity:Option[Entity] = None;
    var Name: String = "";
    var ClassName: String = "";
    protected var parent:Option[BaseControl] = None;
    protected var childrenList: ArrayList[BaseControl] = new ArrayList[BaseControl]();
    protected var bindItemList:ArrayList[BindingItem] = ArrayList();
    protected var bindingInstList:ArrayBuffer[BindingInst] = ArrayBuffer.empty

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
          this.enterBindItems();
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
          if(curItem.sourceType == BindingSource.Data) {
              this.bindingDataContext(curItem);
          }
       }
    }

    def bindingDataContext(item:BindingItem):Unit = {
        val dataCtx = this.findDataContext();
        if(dataCtx != null) {
          this.bindingInstList.find(_.item == item).foreach(DataBindingManager.removeInst);
          DataBindingManager.binding(dataCtx,this,item) match {
            case Success(Some(inst)) => this.bindingInstList += inst
            case Failure(exception) => System.err.println(exception)
            case _ => {}
          }
        }
    }

    def enterBindItems():Unit = {
      for(idx <- 0 until this.bindItemList.size()) {
        val curItem = this.bindItemList.get(idx);
        curItem.sourceType match {
          case BindingSource.Owner => 
            if(this.templateOwner.isDefined) {
              DataBindingManager.binding(this.templateOwner.get,this,curItem) match {
                case Success(Some(inst)) => this.bindingInstList += inst
                case Failure(exception) => System.err.println(exception)
                case _ => {}
              }
            }
          case BindingSource.Data => this.bindingDataContext(curItem)
        }
      }
    }

    

    def onBindSourceChanged(src:INotifyPropertyChanged,name:String,newValue:Any,param:Any):Unit = {
       val curItem = param.asInstanceOf[BindingItem];
       val dstField = Assembly.getTypeInfo(this).flatMap(_.getField(curItem.dstKey));
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
      this.bindingInstList.foreach(DataBindingManager.removeInst);
      this.bindingInstList.clear();
    }
}