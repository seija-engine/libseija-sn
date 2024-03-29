package com.seija.ui.binding
import scala.collection.mutable.ArrayBuffer;
import scala.collection.{Seq,IterableOnce};
class ObservableList[T] extends INotifyCollectionChanged with IndexedSeq[T] {
    protected var list:ArrayBuffer[T] = ArrayBuffer.empty
    
    def length:Int = list.length
    override def iterator: Iterator[T] = this.list.iterator
    override def apply(i: Int): T = this.list(i)

    def add(value:T):Unit = {
       this.list.addOne(value);
       val notify = NotifyCollectionChanged.Add(value,this.length - 1);
       this.callChanged(notify);
    }

    def insert(index:Int,value:T):Unit = {
        this.list.insert(index,value);
        val notify = NotifyCollectionChanged.Add(value,index);
        this.callChanged(notify);
    }

    def update(index:Int,value:T):Unit = {
        this.list.update(index,value);
        val notify = NotifyCollectionChanged.Replace(index,value);
        this.callChanged(notify);
    }
    
    def removeAt(idx:Int):Unit = {
        val oldItem = this.list.remove(idx);
        this.callChanged(NotifyCollectionChanged.Remove(oldItem,idx))
    }

    def move(oldIndex:Int,newIndex:Int):Unit = {
        if(oldIndex == newIndex) return;
        val item = this.list.remove(oldIndex);
        this.list.insert(newIndex,item);
        this.callChanged(NotifyCollectionChanged.Move(item,oldIndex,newIndex))
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

object ObservableList {
    def from[T](iter:IterableOnce[T]):ObservableList[T] = {
      val lst = ObservableList[T]();
      lst.list = ArrayBuffer.from(iter);
      lst
    }
}