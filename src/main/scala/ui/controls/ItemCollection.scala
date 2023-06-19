package ui.controls
import scala.collection.IndexedSeq;
import ui.binding.{INotifyCollectionChanged,CollectionChangedCallBack};
import ui.binding.NotifyCollectionChangedEventArgs
import java.util.ArrayList
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import ui.binding.CollectionChangedAction

trait IDataElementGenerator {
  def genElement(data:Any):Try[UIElement];
}

case class ItemCollection(elemGen:IDataElementGenerator,var parent:UIElement) {
    protected var cacheLst:IndexedSeq[Any] = IndexedSeq.empty;
    protected var notifyList:Option[INotifyCollectionChanged] = None;
    protected var cacheElementList:ArrayBuffer[UIElement] = ArrayBuffer.empty;
    var changedCallBack:Option[CollectionChangedCallBack] = None;

    def getDataList:IndexedSeq[Any] = this.cacheLst;

    def setItemSource(lst:IndexedSeq[Any]):Unit = {
       this.cacheLst = lst;
       if(this.cacheLst.isInstanceOf[INotifyCollectionChanged]) {
          val notifyList = this.cacheLst.asInstanceOf[INotifyCollectionChanged];
          this.notifyList = Some(notifyList);
          notifyList.addChangedHandler(this.OnCollectionChanged);
       }
    }

    protected def OnCollectionChanged(sender:INotifyCollectionChanged,args:NotifyCollectionChangedEventArgs):Unit = {
        this.changedCallBack.foreach(_(sender,args));
    }

    def release():Unit = {
        this.notifyList.foreach {v => 
          v.removeChangedHandler(this.OnCollectionChanged);
        }
    }
}


case class ItemElementListMgr(val parent:UIElement,val collection:ItemCollection) {
    collection.changedCallBack = Some(OnCollectionChanged);
    def start():Unit = {
       for(data <- this.collection.getDataList) {
         val newElement = collection.elemGen.genElement(data);
         newElement match {
          case Success(value) => parent.addChild(value);
          case Failure(exception) => System.err.println(exception.toString());
         }
       }
    }

    protected def OnCollectionChanged(sender:INotifyCollectionChanged,args:NotifyCollectionChangedEventArgs):Unit = {
       args.action match
        case CollectionChangedAction.Add => {
          val newElement = this.collection.elemGen.genElement(args.newItem);
          newElement match {
            case Success(value) => {
              value.Enter();
              parent.insertChild(args.newStartingIndex,value)
              parent.getEntity().get.insertChild(value.getEntity().get,args.newStartingIndex);
            }
            case Failure(exception) => System.err.println(exception.toString());
          }
        }
        case CollectionChangedAction.Replace => {
          parent.children(args.newStartingIndex).dataContext = args.newItem;
        }
        case CollectionChangedAction.Remove => {
          val oldElement = parent.children.remove(args.oldStartingIndex);
          oldElement.Release();
        }
        case CollectionChangedAction.Move => {

        }
        case CollectionChangedAction.Clear => {
          parent.children.foreach(_.Release());
          parent.children.clear();
        }
       
    }
}