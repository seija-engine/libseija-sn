package sxml.vm

import scala.util.Try

class VMContext(vm:SXmlVM) {
    val ownedVM:SXmlVM = vm
    val stack:VMStack = VMStack()

    
    def execute():Try[Unit] = Try {
        val lastCallStack = this.stack.frames.last
        lastCallStack.execute_()
    }
}