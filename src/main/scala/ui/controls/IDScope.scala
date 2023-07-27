package ui.controls

import scala.collection.mutable.HashMap

class IDScope {
    private val IdNameDict:HashMap[String,UIElement] = HashMap.empty

    def apply(id:String):Option[UIElement] = {
        this.IdNameDict.get(id)
    }

    def addElement(id:String,element:UIElement):Unit = {
        this.IdNameDict.put(id,element)
    }

    def removeElement(id:String):Option[UIElement] = {
        this.IdNameDict.remove(id)
    }
}