package ui
import _root_.core.Entity;
import java.util.ArrayList
import _root_.core.IFromString
import java.util.ArrayList;
import java.util.HashMap
import _root_.core.reflect.Assembly;


class BaseControl extends INotifyPropertyChanged with Cloneable {
    var templateOwner:Option[BaseControl] = None;
    protected var isEntered:Boolean = false;
    protected var entity:Option[Entity] = None;
    var Name: String = "";
    var ClassName: String = "";
    protected var parent:Option[BaseControl] = None;
    protected var childrenList: ArrayList[BaseControl] = new ArrayList[BaseControl]();
    protected var bindItemList:ArrayList[BindingItem] = ArrayList();
    protected var bindObjectList:ArrayList[INotifyPropertyChanged] = ArrayList()

    def setParent(parent:Option[BaseControl]) = this.parent = parent;

    def getEntity():Option[Entity] = this.entity

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

    def addBindItem(item: BindingItem): Unit = {
        this.bindItemList.add(item);
    }
    
    def Enter():Unit = {
        if(!this.isEntered) {
          this.isEntered = true;
          this.applyBindItems();
          this.OnEnter();
        }
        this.childrenList.forEach(_.Enter());
    }

    def applyBindItems():Unit = {
        if(this.bindItemList.size() == 0) return;
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
             case BindingSource.Data => {

             }
           }
        }
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

    def OnEnter() = {}

    def Exit():Unit = {
        this.bindObjectList.forEach(item => item.removePropertyChangedHandler(this.onBindSourceChanged));
    }
}