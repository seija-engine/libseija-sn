package ui.visualState
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import ui.resources.Setter
import scala.collection.mutable.Growable;

class VisualState derives ReflectType {
    var Setters:SettersGroup = SettersGroup()
}

class SettersGroup extends Growable[Setter] derives ReflectType {
    var setters:ArrayBuffer[Setter] = ArrayBuffer[Setter]()
    def addOne(elem: Setter): this.type = {
        setters += elem
        this
    }

    def clear(): Unit = {
        setters.clear()
    }
}