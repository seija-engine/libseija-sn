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
 
  private val stringReadeReadAttrPtr = LibSeija.getFunc[CFuncPtr6[Ptr[Byte],Ptr[Boolean],Ptr[Int],Ptr[Ptr[Byte]],Ptr[Int],Ptr[Ptr[Byte]],Boolean]]("xml_reader_read_attr");

  private val xmlGetLastErrorPtr = LibSeija.getFunc[CFuncPtr1[Ptr[Byte],CString]]("xml_get_last_error");

  def xmlReaderFromString(str:String):Ptr[Byte] = Zone { implicit z =>
    xmlReaderFromStringPtr(toCString(str))
  }

  def stringReaderReadEvent(reader:Ptr[Byte]):Either[String,XmlEvent] = { 
    val ptrType = stackalloc[Byte]()
    val ptrLen = stackalloc[Int]()
    val ptrptrByte = stackalloc[Ptr[Byte]]()
    val isSucc = stringReaderReadEventPtr(reader,ptrType,ptrLen,ptrptrByte);
    if(isSucc == false) return Left(getLastError(reader))
    val event = !ptrType match {
      case 1 => XmlEvent.StartElement(fromRustString(!ptrptrByte,!ptrLen))
      case 2 => XmlEvent.EndElement(fromRustString(!ptrptrByte,!ptrLen))
      case 3 => XmlEvent.EmptyElement(fromRustString(!ptrptrByte,!ptrLen))
      case 4 => XmlEvent.Text("")
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
}
