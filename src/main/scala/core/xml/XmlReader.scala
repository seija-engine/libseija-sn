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

 
case class XmlReader(private val rawPtr:Ptr[Byte],isString:Boolean) {
   var cacheEvent:Option[XmlEvent] = None;

   def nextEvent():Try[XmlEvent] = {
      if(this.cacheEvent.isDefined) {
         val ret = this.cacheEvent.get;
         this.cacheEvent = None;
         return Success(ret)
      }
      FFIXml.xmlReaderReadEvent(rawPtr,isString).left.map(new Exception(_)).toTry
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

   def nextAttr():Try[Option[(String,String)]] = FFIXml.readerReadAttr(rawPtr)

   def release() = {
      FFIXml.releaseReader(rawPtr,isString)
   }
}

object XmlReader {
  def fromString(str:String):XmlReader = {
    val rawPtr = FFIXml.xmlReaderFromString(str);
    new XmlReader(rawPtr,true)
  }

  def fromFile(filePath:String):Try[XmlReader] = {
     val rawPtr = FFIXml.xmlRederFromFile(filePath);
     rawPtr match
      case Failure(exception) => Failure(exception)
      case Success(value) => {
        Success(XmlReader(value,false))
      }
  }
  
}