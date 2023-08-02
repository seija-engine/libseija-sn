package sxml.vm

case class CompiledModule(val function: CompiledFunction);

case class CompiledFunction(val args:Long,val instructions:Array[Instruction]) {
   
}