package com.seija.sxml.vm.stdlib
import com.seija.sxml.vm.ExternModule
import java.util.HashMap
import scala.collection.mutable
import com.seija.sxml.vm.VMContext
import com.seija.sxml.vm.VMValue

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

