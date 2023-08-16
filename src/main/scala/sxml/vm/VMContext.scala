package sxml.vm

import scala.util.Try

class VMContext(vm:SXmlVM) {
    val ownedVM:SXmlVM = vm
    val stack:VMStack = VMStack()

    
    def execute():Try[Unit] = Try {
        var curCallStack:Option[VMCallStack] = this.stack.frames.lastOption
        while(curCallStack.isDefined) {
           curCallStack = curCallStack.get.execute_().get
        }
        println("**********END**********")
        println(this.stack.values.mkString("\r\n"))
    }
}