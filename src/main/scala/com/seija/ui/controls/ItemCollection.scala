package com.seija.ui.controls
import scala.collection.IndexedSeq;
import com.seija.ui.binding.{INotifyCollectionChanged,CollectionChangedCallBack};
import com.seija.ui.binding.NotifyCollectionChangedEventArgs
import java.util.ArrayList
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.seija.ui.binding.CollectionChangedAction



case class ItemCollection() {
    protected var cacheLst:IndexedSeq[Any] = IndexedSeq.empty
    protected var notifyList:Option[INotifyCollectionChanged] = None
    var changedCallBack:Option[CollectionChangedCallBack] = None

    def getDataList:IndexedSeq[Any] = this.cacheLst

    def setItemSource(lst:IndexedSeq[Any]):Unit = {
       this.cacheLst = lst
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

/*
case class ItemElementListMgr(val parent:UIElement,val collection:ItemCollection) {
    collection.changedCallBack = Some(OnCollectionChanged);
    def start():Unit = {
       for(data <- this.collection.getDataList) {
         val newElement = collection.elemGen.genElement(data);
         
         newElement match {
          case Success(value) => parent.addChild(value);
          case Failure(exception) => slog.error(exception);
         }
       }
    }

    protected def OnCollectionChanged(sender:INotifyCollectionChanged,args:NotifyCollectionChangedEventArgs):Unit = {
       args.action match
        case CollectionChangedAction.Add => {
          val newElement = this.collection.elemGen.genElement(args.newItem);
          newElement match {
            case Success(value) => {
              value.Enter()
              parent.insertChild(args.newStartingIndex,value)
              value.getEntity().get.setParent(parent.getEntity());
              //parent.getEntity().get.insertChild(value.getEntity().get,args.newStartingIndex);
            }
            case Failure(exception) => slog.error(exception)
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
          val dataList = this.collection.getDataList;
          val a = args.newStartingIndex;
          val b = args.oldStartingIndex;
          val (startIdx,endIdx) = if(a > b) (b,a) else (a,b)
          startIdx.to(endIdx).foreach {index => 
            parent.children(index).dataContext = dataList(index);  
          }
        }
        case CollectionChangedAction.Clear => {
          parent.children.foreach(_.Release());
          parent.children.clear();
        }
       
    }
}*/