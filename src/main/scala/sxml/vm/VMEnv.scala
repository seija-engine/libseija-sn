package sxml.vm
import scala.collection.mutable.HashMap
import scala.PolyFunction
import scala.collection.mutable.ArrayBuffer


case class ModuleInfo(val vars:HashMap[String,VMValue] = HashMap.empty)

class VMEnv {
  private val moduleDict:HashMap[String,ModuleInfo] = HashMap.empty
  private val preludeDict:HashMap[String,String]  = HashMap.empty
  val importer = Importer()

  def getModule(modName:String):Option[ModuleInfo] = this.moduleDict.get(modName)

  def getPreludeLibName(name:String):Option[String] = this.preludeDict.get(name)

  def addModuleVar(modName:String,varName:String,value:VMValue):Unit = {
    val curModuleInfo = this.moduleDict.getOrElseUpdate(modName,ModuleInfo())
    curModuleInfo.vars.put(varName,value)
  }

  def getModuleVar(modName:String,varName:String):VMValue = {
    this.moduleDict.get(modName).flatMap(_.vars.get(varName)).getOrElse(VMValue.NIL())
  }

  def addExternModule(externModule:ExternModule):Boolean = {
    if(this.moduleDict.contains(externModule.modName)) return false;
    for(preludeName <-externModule.preludeList) {
      this.preludeDict.put(preludeName,externModule.modName)
    }
    this.moduleDict.put(externModule.modName,ModuleInfo(externModule.varDict));
    true
  }
}

case class ExternModule(
  val modName:String,
  val varDict:HashMap[String,VMValue],
  val preludeList:ArrayBuffer[String] = ArrayBuffer.empty
) {

  inline def addFunc[T](inline func:T,isPrelude:Boolean = false):Unit = {
    val wrapData = wrapExternFunc(func);
    if(!this.varDict.contains(wrapData.name)) {
      this.varDict.put(wrapData.name,VMValue.VMExternFunc(wrapData))
      if(isPrelude) {
        this.preludeList += wrapData.name;
      } 
    }
  }

}

