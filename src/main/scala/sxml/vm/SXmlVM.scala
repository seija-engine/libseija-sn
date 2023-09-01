package sxml.vm

import scala.util.Try
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.collection.mutable.HashMap
import sxml.vm.stdlib.IOModule

class SXmlVM {
    val env:VMEnv = VMEnv()
    val context = VMContext(this)

    def addSearchPath(path:String):Unit = {
        this.env.importer.addSearchPath(path)
    }

    def addBuildinModule():Unit = {
        this.env.addExternModule(IOModule.externModule())
    }

    def callFile(fsPath:String):Try[VMValue] = Try  {
        this.callCodeSource(fsPath,Source.fromFile(fsPath)).get
    }

    def callString(codeString:String,modName:String = ""):Try[VMValue] = Try {
        this.callCodeSource(modName,Source.fromString(codeString)).get
    }

    def callCodeSource(codeName:String,source:Source):Try[VMValue] = Try {
        val parser = sxml.parser.Parser.fromSource(codeName,source)
        val astModule = parser.parseModule().get
        val trans = sxml.compiler.Translator()
        val transModule = trans.translateModule(astModule).get
        val compiler = sxml.compiler.Compiler(this.env)
        val module = compiler.compileModule(transModule).get
        //module.function.debugShow(0)
        this.callModule(module).get
    }

    def callModule(module:CompiledModule):Try[VMValue] = Try {
        for(importItem <- module.imports) {
            if(this.env.getModule(importItem.libName).isEmpty) {
                this.env.importer.importByName(importItem.libName,this).get
            }
        }
        
        val closureData = this.moduleToClosureData(module)
        this.callThunk(closureData).get
    }

    def callThunk(closure:ClosureData):Try[VMValue] = Try {
        val closureState = ClosureState(closure,0)
        this.context.stack.values.addOne(VMValue.VMClosure(closure))
        this.context.stack.enterCallStack(1,closureState)
        this.context.execute().get
        val lastValue = this.context.stack.values.last
        this.context.stack.values.pop();
        lastValue
    }

    protected def moduleToClosureData(module:CompiledModule):ClosureData = {
        ClosureData(module.function,ArrayBuffer())
    }

    
    
}
