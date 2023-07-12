package ui
import _root_.core.{Entity, FFISeijaCore}
import core.FFISeijaUI
import scala.scalanative.unsafe.*
import scala.scalanative.libc.stddef
import scala.collection.mutable


object LayoutUtils {
  private var postLayoutList:mutable.ArrayBuffer[(Int) => Unit] = mutable.ArrayBuffer.empty
  def init(worldPtr:Ptr[Byte]):Unit = {
    FFISeijaUI.SetOnPostLayoutProcess(worldPtr,
      CFuncPtr.toPtr(CFuncPtr2.fromScalaFunction(postLayoutProcess)))
  }
  private var _vecPtr:Ptr[Byte] = stddef.NULL

  def addPostLayout(callFN: Int => Unit):Unit = {
    this.postLayoutList += callFN
  }

  def removePostLayout(callFN:Int => Unit):Unit = {
    this.postLayoutList.filterInPlace(_ == callFN)
  }
  protected def postLayoutProcess(step:Int,vecPtr:Ptr[Byte]):Unit = {
    _vecPtr = vecPtr
    this.postLayoutList.foreach(_(step))
    _vecPtr = stddef.NULL
  }

  def isDirty(entity:Entity, index:Int):Boolean = {
    val frame = _root_.core.Time.getFrameCount()
    _root_.core.FFISeijaCore.isFrameDirty(entity,frame,index)
  }

  def addPostLayoutDirtyEntity(entity:Entity):Boolean = {
    if(_vecPtr == stddef.NULL) return false
    FFISeijaUI.vecAddU64(_vecPtr,entity.id)
    true
  }
}
