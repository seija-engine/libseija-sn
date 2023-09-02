package ui.xml
import scala.collection.mutable;
import scala.collection.mutable.HashMap
import core.reflect.*;
import scala.collection.mutable.ArrayBuffer
import scala.util.boundary,boundary.break;
import scala.util.Failure
import scala.util.Try

enum NSValue {
    case Const(value: String) extends NSValue
    case Ruled(value: NSRuledValue) extends NSValue
    
    def resolver(name:String):Option[String] = this match {
        case Const(value) => Some(value)
        case Ruled(value) => value.resolver(name)
    }
}

class XmlNSResolver {
   private var nsMap: HashMap[String, NSValue] = HashMap[String, NSValue]();
   
   def addConst(name: String, value: String) = {
       nsMap.put(name, NSValue.Const(value));
   }

   def addRuled(name: String, value: NSRuledValue) = {
       nsMap.put(name, NSValue.Ruled(value));
   }

   def resolver(name: String):Option[String] = {
     val names = name.split(":");
     val (nsName,typName) = if(names.length == 1) ("", names(0)) else (names(0), names(1));
     val nsValue = nsMap.get(nsName);
     if(nsValue.isEmpty) return None;
     nsValue.get.resolver(typName)
   }

   def resolverTypeInfo(name:String):Try[TypeInfo] = {
      this.resolver(name).toRight(new NotFoundTypeInfoException(name)).toTry.flatMap(Assembly.getTry(_))
   }
}

class NSRuledValue {
   private var fullMap: HashMap[String, String] = HashMap[String, String]();
   private var prefixList: ArrayBuffer[String] = ArrayBuffer[String]();
   def addImport(pathString: String) = {
    if(pathString.endsWith(".*")) {
        val prefix = pathString.substring(0, pathString.length() - 2);
        prefixList.addOne(prefix);
    } else {
        val names = pathString.split("\\.");
        this.fullMap.put(names(names.length - 1), pathString);    
    }
   }

   def resolver(name:String):Option[String] =  {
      if(fullMap.contains(name)) return Some(fullMap.get(name).get);
      prefixList.find((prefix) => Assembly.has(s"${prefix}.${name}")).map((prefix) => s"${prefix}.${name}")
   }
}


object XmlNSResolver {
    val default = { 
        val ruled = new NSRuledValue();
        ruled.addImport("ui.controls.*")
        ruled.addImport("ui.resources.*")
         ruled.addImport("ui.visualState.*")
        ruled.addImport("ui.core.Thickness")
        val resolver = new XmlNSResolver();
        resolver.addRuled("", ruled);
        resolver 
    };
}

