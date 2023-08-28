package sxml.vm

import scala.collection.mutable.HashMap

class ModuleInfo {

}

class VMEnv {
  val moduleDict:HashMap[String,ModuleInfo] = HashMap.empty
  val importer = Importer()

  def getModule(modKey:String):Option[ModuleInfo] = this.moduleDict.get(modKey)
}