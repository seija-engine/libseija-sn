package core.xml
import scalanative.unsafe._
import core.LibSeija
import java.nio.charset.{Charset, StandardCharsets}
import scalanative.unsigned._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
object FFIXml {
  private val xmlReaderFromStringPtr = LibSeija.getFunc[CFuncPtr1[CString,Ptr[Byte]]]("xml_reader_from_string");
 
  private val stringReaderReadEventPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Ptr[Byte],Ptr[Int],Ptr[Ptr[Byte]],Boolean]]("string_reader_read_event");
  private val fileReaderReadEventPtr = LibSeija.getFunc[CFuncPtr4[Ptr[Byte],Ptr[Byte],Ptr[Int],Ptr[Ptr[Byte]],Boolean]]("file_reader_read_event");
  private val stringReadeReadAttrPtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Ptr[Boolean],Ptr[Int],Ptr[Ptr[Byte]],Ptr[Int],Ptr[Ptr[Byte]],Boolean]]("xml_reader_read_attr");

  private val xmlReaderFromFilePtr = LibSeija.getFunc[CFuncPtr1[CString,Ptr[Byte]]]("xml_reader_from_file");

  private val xmlGetLastErrorPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],CString]]("xml_get_last_error");

  private val xmlReaderReleaseString = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("xml_reader_release_string");
  private val xmlReaderReleaseFile = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],Unit]]("xml_reader_release_file");

  def xmlReaderFromString(str:String):Ptr[Byte] = Zone { implicit z =>
    xmlReaderFromStringPtr(toCString(str))
  }

  def xmlRederFromFile(path:String):Try[Ptr[Byte]] = Zone { implicit z =>
      val ptrReader = xmlReaderFromFilePtr(toCString(path));
      if(ptrReader == null) {
        Failure(new Throwable(s"xml not found path ${path}"))
      } else {
        Success(ptrReader)
      }
  }

  def xmlReaderReadEvent(reader:Ptr[Byte],isString:Boolean):Either[String,XmlEvent] = { 
    val ptrType = stackalloc[Byte]()
    val ptrLen = stackalloc[Int]()
    val ptrptrByte = stackalloc[Ptr[Byte]]()
    val isSucc = if(isString) {
      stringReaderReadEventPtr(reader,ptrType,ptrLen,ptrptrByte)
    } else {
      fileReaderReadEventPtr(reader,ptrType,ptrLen,ptrptrByte)
    }
    if(isSucc == false) return Left(getLastError(reader))
    val event = !ptrType match {
      case 1 => XmlEvent.StartElement(fromRustString(!ptrptrByte,!ptrLen))
      case 2 => XmlEvent.EndElement(fromRustString(!ptrptrByte,!ptrLen))
      case 3 => XmlEvent.EmptyElement(fromRustString(!ptrptrByte,!ptrLen))
      case 4 => XmlEvent.Text(fromRustString(!ptrptrByte,!ptrLen))
      case 5 => XmlEvent.Comment("")
      case 6 => XmlEvent.EOF
      case 7 => XmlEvent.Unkonwn
    }
    Right(event)
  }

  def readerReadAttr(reader:Ptr[Byte]):Try[Option[(String,String)]] = {
    val ptrIsError = stackalloc[Boolean]()
    val ptrKeyLen = stackalloc[Int]()
    val ptrptrKey = stackalloc[Ptr[Byte]]()
    val ptrValueLen = stackalloc[Int]()
    val ptrptrValue = stackalloc[Ptr[Byte]]()
    val isNotEnd = stringReadeReadAttrPtr(reader,ptrIsError,ptrKeyLen,ptrptrKey,ptrValueLen,ptrptrValue)
    if(!ptrIsError == true) {
      return Failure(new Throwable(getLastError(reader)))
    }
    if(!isNotEnd) return Success(None);
    val tagString = fromRustString(!ptrptrKey,!ptrKeyLen);
    val valueString =  if(!ptrValueLen > 0) {
      fromRustString(!ptrptrValue,!ptrValueLen);
    } else { "" }
    
    Success(Some(tagString,valueString))
  }

  def getLastError(reader:Ptr[Byte]):String = {
      fromCString(xmlGetLastErrorPtr(reader))
  }

  def fromRustString(ptr:Ptr[Byte],len:Int):String = {
    val bytes = new Array[Byte](len)
    scalanative.runtime.libc.memcpy(bytes.at(0), ptr, len.toULong)
    val endString =  new String(bytes, Charset.defaultCharset());
    endString
  }

  def releaseReader(ptr:Ptr[Byte],isString:Boolean) = {
    if(isString) xmlReaderReleaseString(ptr) else xmlReaderFromFilePtr(ptr)
  }
}
