package sxml.vm

import scala.util.Try
import scala.collection.mutable.ArrayBuffer

class SXmlVM {
    val context = VMContext(this)

    def runModule(module:CompiledModule):Try[Unit] = Try {
        val closureData = this.moduleToClosureData(module)
        this.callThunk(closureData)
    }

    def callThunk(closure:ClosureData):Unit = {
        val closureState = ClosureState(closure,0)
        this.context.stack.enterCallStack(0,closureState)
        this.context.execute()
        this.context.stack.exitCallStack()
    }

    protected def moduleToClosureData(module:CompiledModule):ClosureData = {
        println(module.moduleGlobals.foreach(println))
        ClosureData(module.function,ArrayBuffer())
    }
}
