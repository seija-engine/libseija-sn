package ui.binding
import scala.collection.mutable.ArrayBuffer;
import scala.collection.Seq;
class ObservableList[T] extends INotifyCollectionChanged with Seq[T] {
    protected var list:ArrayBuffer[T] = ArrayBuffer.empty
    
    def length:Int = list.length
    override def iterator: Iterator[T] = this.list.iterator
    override def apply(i: Int): T = this.list(i)

    def add(value:T):Unit = {
       this.list.addOne(value);
       val notify = NotifyCollectionChanged.Add(value,this.length - 1);
       this.callChanged(notify);

    }
    
    def removeAt(idx:Int):Unit = {
        val oldItem = this.list.remove(idx);
        this.callChanged(NotifyCollectionChanged.Remove(oldItem,idx))
    }

    def remove(item:T):Boolean = {
       val findIdx = this.list.lastIndexOf(item);
       if(findIdx >= 0) {
          this.removeAt(findIdx);
          return true;
       }
       false
    }

    def clear():Unit = {
        this.list.clear();
        this.callChanged(NotifyCollectionChanged.Clear());
    }
}