package ui.binding
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;
enum CollectionChangedAction(val value:Int) {
    case Add extends CollectionChangedAction(0)
    case Remove extends CollectionChangedAction(1)
    case Replace extends CollectionChangedAction(2)
    case Move extends CollectionChangedAction(3)
    case Clear extends CollectionChangedAction(4)
}

case class NotifyCollectionChangedEventArgs(
    val action:CollectionChangedAction,
    val isSingleItem:Boolean = false,
    val newItem:Any = null,
    val oldItem:Any = null,
    val newItems:Seq[Any] = Seq.empty,
    val oldItems:Seq[Any] = Seq.empty,
    val newStartingIndex:Int = 0,
    val oldStartingIndex:Int = 0
);

object NotifyCollectionChanged {
    def Add(newItem:Any,newStartingIndex:Int):NotifyCollectionChangedEventArgs = {
        NotifyCollectionChangedEventArgs(CollectionChangedAction.Add,true,newItem = newItem,newStartingIndex = newStartingIndex)
    }

    def Replace(index:Int,newValue:Any):NotifyCollectionChangedEventArgs = {
        NotifyCollectionChangedEventArgs(CollectionChangedAction.Replace,true,newStartingIndex = index,newItem = newValue)
    }

    def Clear():NotifyCollectionChangedEventArgs = {
        NotifyCollectionChangedEventArgs(CollectionChangedAction.Clear,true)
    }

    def Remove(oldItem:Any,idx:Int):NotifyCollectionChangedEventArgs = {
        NotifyCollectionChangedEventArgs(CollectionChangedAction.Remove,true,oldItem = oldItem,oldStartingIndex = idx)
    }

    def Move(item:Any,oldIndex:Int,newIndex:Int):NotifyCollectionChangedEventArgs = {
        NotifyCollectionChangedEventArgs(CollectionChangedAction.Move,
        true,oldItem = item,
        oldStartingIndex = oldIndex,
        newStartingIndex = newIndex)
    }
}

type CollectionChangedCallBack = (INotifyCollectionChanged, NotifyCollectionChangedEventArgs) => Unit;

trait INotifyCollectionChanged {
    var handles: ArrayBuffer[CollectionChangedCallBack] = ArrayBuffer.empty

    def addChangedHandler(handler: CollectionChangedCallBack):Unit = {
        this.handles.addOne(handler);
    }

    def removeChangedHandler(handler:CollectionChangedCallBack):Unit = {
       val idx = this.handles.lastIndexOf(handler);
       if(idx >= 0) { this.handles.remove(idx); }
    }

    def callChanged(args:NotifyCollectionChangedEventArgs): Unit = {
        for (handle <- this.handles) {
            handle(this,args);
        }
    }
}