package sxml.vm

import scala.collection.mutable.HashMap

class ModuleInfo {
  val vars:HashMap[String,VMValue] = HashMap.empty
}

class VMEnv {
  val moduleDict:HashMap[String,ModuleInfo] = HashMap.empty
  val importer = Importer()

  def getModule(modName:String):Option[ModuleInfo] = this.moduleDict.get(modName)

  def addModuleVar(modName:String,varName:String,value:VMValue):Unit = {
    val curModuleInfo = this.moduleDict.getOrElseUpdate(modName,ModuleInfo())
    curModuleInfo.vars.put(varName,value)
  }

  def getModuleVar(modName:String,varName:String):VMValue = {
    this.moduleDict.get(modName).flatMap(_.vars.get(varName)).getOrElse(VMValue.NIL())
  }
}