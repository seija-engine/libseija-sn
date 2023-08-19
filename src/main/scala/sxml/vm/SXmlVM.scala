package sxml.vm

import scala.util.Try
import scala.collection.mutable.ArrayBuffer

class SXmlVM {
    val context = VMContext(this)

    def runModule(module:CompiledModule):Try[VMValue] = Try {
        val closureData = this.moduleToClosureData(module)
        this.callThunk(closureData).get
    }

    def callThunk(closure:ClosureData):Try[VMValue] = Try {
        val closureState = ClosureState(closure,0)
        this.context.stack.values.addOne(VMValue.VMClosure(closure))
        this.context.stack.enterCallStack(1,closureState)
        this.context.execute().get
        val lastValue = this.context.stack.values.last
        this.context.stack.values.remove(this.context.stack.values.length - 1)
        lastValue
    }

    protected def moduleToClosureData(module:CompiledModule):ClosureData = {
        
        ClosureData(module.function,ArrayBuffer())
    }
}
