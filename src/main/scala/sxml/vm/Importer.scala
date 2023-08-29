package sxml.vm
import scala.collection.mutable.ArrayBuffer
import java.io.File
import java.nio.file.{Path,Paths}
import java.nio.file.Files
import scala.io.Source
import scala.util.Try
import scala.util.Success

class Importer {
    val pathList:ArrayBuffer[Path] = ArrayBuffer.empty
    def addSearchPath(path:String):Unit = {
       val p = Path.of(path)
       this.pathList.addOne(p)
    }

    def importByName(modName:String,vm:SXmlVM):Try[Unit] = Try {
        val modFileName = modName.replace('.',File.separatorChar) + ".clj";
        val fstPath = pathList.map(path =>  path.resolve(modFileName) ).find(Files.exists(_));
        if(fstPath.isEmpty) throw Exception(s"not found mod:${modFileName}");
        val fileSource = Source.fromFile(fstPath.get.toFile())

        val parser = sxml.parser.Parser.fromSource(modName,fileSource)
        val astModule = parser.parseModule().get
        val trans = sxml.compiler.Translator()
        val transModule = trans.translateModule(astModule).get
        val compiler = sxml.compiler.Compiler()
        val module = compiler.compileModule(transModule).get
        //module.function.debugShow(0)
        vm.callModule(module).get
    }
}