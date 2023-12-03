package com.seija.core
import scala.collection.mutable.ArrayBuffer;
import scala.scalanative.unsigned._;

case class UpdateInfo(
    val func:(Float) => Unit,
    var isDelete:Boolean
);

object UpdateMgr {
  private var updaterList:ArrayBuffer[UpdateInfo] = ArrayBuffer.empty;
  
  def add(f:(Float) => Unit):Unit = {
    val info = UpdateInfo(f,false)
    this.updaterList.addOne(info);
  }

  def remove(f:(Float) => Unit):Unit = {
    this.updaterList.find(_.func == f).foreach {info => 
       info.isDelete = true;    
    }
  }

  def update():Unit = {
     var hasRemove = false;
     for(info <- this.updaterList) {
        info.func(Time.getDeltaTime());
        if(info.isDelete) { hasRemove = true; }
     }

     if(hasRemove) {
        this.updaterList.filterInPlace(info => !info.isDelete);
     }
  }
}