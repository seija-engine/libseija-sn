package sxml.vm
import scala.collection.mutable.ArrayBuffer
case class CompiledModule(
    val moduleGlobals:Array[Symbol],
    val function: CompiledFunction);

case class CompiledFunction(
    val args:Int,
    val id:Symbol,
    val strings:ArrayBuffer[String],
    val instructions:ArrayBuffer[Instruction]) {
   
}