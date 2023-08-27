package sxml.vm
import scala.collection.mutable.ArrayBuffer

case class Importer(
    val vm:SXmlVM,
    val pathList:ArrayBuffer[String] = ArrayBuffer.empty
)