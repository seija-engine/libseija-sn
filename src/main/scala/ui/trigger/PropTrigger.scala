package ui.trigger

import scala.util.Try
import sxml.vm.VMValue

case class PropTrigger(propName:String) {

}

object PropTrigger {
    def fromScript(prop:VMValue,lst:VMValue):Try[PropTrigger] = Try {
        val propName = prop.toScalaValue().asInstanceOf[String]
        val anyList = lst.toScalaValue().asInstanceOf[Vector[Any]]
        for(idx <- 0.until(anyList.length,2)) {
            val valueKey = anyList(idx)
            println(s"value:${valueKey}")
        }
        PropTrigger(propName)
    }
}