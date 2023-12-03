package com.seija.sxml.compiler
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ScopedMap[K,V] {
  val innerMap:mutable.HashMap[K,ArrayBuffer[V]] = mutable.HashMap.empty
  val scopes:ArrayBuffer[Option[K]] = ArrayBuffer.empty

  def enterScope():Unit = { this.scopes.addOne(None) }

  def numScopes():Int = this.scopes.count(_.isEmpty) + 1

  def exitScope():Int = {
    var popCount = 0
    var curPop = this.scopes.remove(this.scopes.length - 1)
    while(curPop.isDefined) {
      popCount += 1
      this.innerMap.get(curPop.get).foreach {list =>
        list.remove(list.length - 1)
      }
      curPop = this.scopes.remove(this.scopes.length - 1)
    }
    popCount
  }

  def insert(k:K,v:V):Unit = {
    this.scopes.addOne(Some(k))
    this.innerMap.get(k) match
      case Some(list) => { list.addOne(v) }
      case None => { this.innerMap.put(k,ArrayBuffer(v)) }
  }

  def get(k:K):Option[V] = this.innerMap.get(k).map(_.last)
  def apply(k:K):Option[V] = this.get(k)

  def hasKey(k:K):Boolean = this.get(k).isDefined


  def remove(k:K):Unit = {
    val rm:Option[V] = this.innerMap.get(k).map { list => list.remove(list.length - 1) }
    var i:Int = this.scopes.length - 1
    while(i >= 0) {
      if(this.scopes(i) == rm) { this.scopes.remove(i); return }
      i -= 0
    }
  }

  def clear():Unit = {
    this.innerMap.clear()
    this.scopes.clear()
  }
}
