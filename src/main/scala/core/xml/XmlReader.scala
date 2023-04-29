package core.xml
import scalanative.unsafe._
import scala.collection.Iterable
import scala.util.Try
import scala.util.Success
import scala.util.Failure
enum XmlEvent {
  case StartElement(name:String)
  case EndElement(name:String)
  case EmptyElement(name:String)
  case Text(text:String)
  case Comment(text:String)
  case EOF
  case Unkonwn

  def IsEOF():Boolean = {
     this match
      case EOF => true
      case _ => false
  }

  def IsEnd(name:String):Boolean = {
    this match
      case EndElement(endName) => endName == name
      case _ => false
  }

  def castStart():Option[String] = {
    this match
      case StartElement(name) => Some(name)
      case _ => None
  }

  def castEmpty():Option[String] = {
    this match
      case EmptyElement(name) => Some(name)
      case _ => None 
    
  }
}


class XmlReader(private val rawPtr:Ptr[Byte]) extends Iterable[XmlEvent] {
   override def iterator: Iterator[XmlEvent] = new XmlReaderIter(rawPtr)
   var cacheEvent:Option[XmlEvent] = None;

   def nextEvent():Try[XmlEvent] = {
      if(this.cacheEvent.isDefined) {
         val ret = this.cacheEvent.get;
         this.cacheEvent = None;
         return Success(ret)
      }
      FFIXml.stringReaderReadEvent(rawPtr).left.map(new Throwable(_)).toTry
      
   }

   def lookNext():Try[XmlEvent] = {
      if(this.cacheEvent.isDefined) {
        return Success(this.cacheEvent.get)
      }
      this.nextEvent().flatMap { newEvent =>
          this.cacheEvent = Some(newEvent)
          Success(newEvent)
      }
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