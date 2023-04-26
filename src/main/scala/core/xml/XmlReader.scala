package core.xml
import scalanative.unsafe._
import scala.collection.Iterable
enum XmlEvent {
  case StartElement(name:String)
  case EndElement(name:String)
  case EmptyElement(name:String)
  case Text(text:String)
  case Comment(text:String)
  case EOF
  case Unkonwn
}

class XmlReader(private val rawPtr:Ptr[Byte]) extends Iterable[XmlEvent] {
   override def iterator: Iterator[XmlEvent] = new XmlReaderIter(rawPtr)

   def nextEvent():Either[String,XmlEvent] = {
      FFIXml.stringReaderReadEvent(rawPtr)
   }
   def nextAttr():Option[(String,String)] = {
     FFIXml.readerReadAttr(rawPtr)
   }
}

case class XmlReaderIter(private val rawPtr:Ptr[Byte]) extends Iterator[XmlEvent] {
  var cacheEvent:XmlEvent = null;
  override def hasNext: Boolean = {
    if(cacheEvent != null) return true;
    FFIXml.stringReaderReadEvent(rawPtr) match {
      case Right(XmlEvent.EOF) => false
      case Left(value) => false
      case Right(value) => { this.cacheEvent = value; true }
    }
  }

  

  override def next(): XmlEvent = {
      if(this.cacheEvent != null) {
         val ret = this.cacheEvent;
         this.cacheEvent = null;
         return ret;
      }
      FFIXml.stringReaderReadEvent(rawPtr) match {
        case Right(XmlEvent.EOF) => null
        case Left(value) => null
        case Right(value) => value
      }
  }


}


object XmlReader {
  def fromString(str:String):XmlReader = {
    val rawPtr = FFIXml.xmlReaderFromString(str);
    new XmlReader(rawPtr)
  }

  
}

/*
FFIXml.stringReaderReadEvent(ptrReader);
    FFIXml.stringReaderReadAttr(ptrReader);
    FFIXml.stringReaderReadAttr(ptrReader);
    
    FFIXml.stringReaderReadAttr(ptrReader);
*/