package ui
import _root_.core.{Entity, FFISeijaCore}
import core.FFISeijaUI
import scala.scalanative.unsafe.*
import scala.scalanative.libc.stddef
import scala.collection.mutable


object LayoutUtils {
  private var postLayoutList:mutable.ArrayBuffer[(Int) => Unit] = mutable.ArrayBuffer.empty
  private var _isInPostLayout:Boolean = false
  var frameSet:mutable.HashSet[Long] = mutable.HashSet.empty
  def isInPostLayout: Boolean = this._isInPostLayout

  private var _postLayoutStep:Int = -1
  def postLayoutStep:Int = this._postLayoutStep
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

  def OnUpdate():Unit = {
    this._postLayoutStep = -1
  }

  protected def postLayoutProcess(step:Int,vecPtr:Ptr[Byte]):Unit = {
    this.frameSet.clear()
    this._postLayoutStep = step
    if(step > 0) {
      println(s"trigger post step:${step}")
    }
    _vecPtr = vecPtr
    _isInPostLayout = true
    this.postLayoutList.foreach(_(step))
    _vecPtr = stddef.NULL
    _isInPostLayout = false
  }

  def isDirty(entity:Entity, index:Int):Boolean = {
    val frame = _root_.core.Time.getFrameCount()
    _root_.core.FFISeijaCore.isFrameDirty(entity,frame,index)
  }

  def addPostLayoutDirtyEntity(entity:Entity):Boolean = {
    if(_vecPtr == stddef.NULL || this.frameSet.contains(entity.id)) return false
    FFISeijaUI.vecAddU64(_vecPtr,entity.id)
    this.frameSet.add(entity.id)
    true
  }
}
