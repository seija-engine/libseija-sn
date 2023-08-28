package sxml.vm
import scala.collection.mutable.ArrayBuffer

case class ImportInfo(
    val libName:String
)

case class CompiledModule(
    val imports:Array[ImportInfo],
    val exports:Array[String],
    //val moduleGlobals:Array[Symbol],
    val function: CompiledFunction
);

case class CompiledFunction(
    val args:Int,
    val id:Symbol,
    val strings:ArrayBuffer[String],
    val instructions:ArrayBuffer[Instruction],
    val innerFunctions:ArrayBuffer[CompiledFunction] = ArrayBuffer.empty) {

  def debugShow(depth:Int):Unit = {
    val white = " ".repeat(depth)
    if(this.strings.length > 0) {
        println(s"${white}strings:${this.strings.mkString}")
    }
    println(s"${white}instr:")
    this.instructions.foreach {instr =>
        println(s"${white}${instr}")    
    }
    if(this.innerFunctions.length > 0) {
        println(s"${white}innerFunction:")
        this.innerFunctions.foreach(_.debugShow(depth + 1))
    }
  }
}