package sxml.vm.stdlib
import sxml.vm.ExternModule
import java.util.HashMap
import scala.collection.mutable
import sxml.vm.VMContext
import sxml.vm.VMValue

object IOModule {

    def println(showValue:VMValue):VMValue = {
        System.out.println(showValue.toString())
        VMValue.NIL()
    }

    def externModule():ExternModule = {
        val ioModule = ExternModule("io",mutable.HashMap());
        ioModule.addFunc(println,true)  
        ioModule
    }
}

