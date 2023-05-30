package ui.xml
import scala.util.Try
import core.xml.XmlElement
import core.reflect.TypeInfo
import core.reflect.DynTypeConv
import core.logError;
import scala.collection.mutable.Growable
import ui.ContentProperty

class XmlObjectParser(val nsResolver: XmlNSResolver = XmlNSResolver.default) {
    def parse(xml: XmlElement): Try[Any] = Try {
      val curTypeInfo = nsResolver.resolverTypeInfo(xml.name).get;
      val curObject = curTypeInfo.create();
      for((k,v) <- xml.attributes) {
        setStringProp(curTypeInfo,curObject,k,v);
      }
      var contentCount = 0;
      for(childElem <- xml.children) {
        val dotIndex = childElem.name.indexOf(".");
        if(dotIndex > 0) {
           val attrName = childElem.name.substring(dotIndex + 1,childElem.name.length());
           setXMLProp(curTypeInfo,attrName,curObject,childElem).logError();         
        } else { contentCount += 1; }
      }
      val contentName = curTypeInfo.getAnnotation[ContentProperty].map(_.name).getOrElse("content");
      val contentField = curTypeInfo.getField(contentName);
      if(contentField.isDefined) {
        
        if(contentCount == 0 && xml.innerText.isDefined) {
        
        } else if(contentCount == 1) {
          xml.children.filter(_.name.indexOf(".") < 0).foreach { childElem =>
            val childObject = this.parse(childElem).logError();
            println(childObject);
          }
        } else if(contentCount > 1) {

        }
      }
      curObject
    }

    def setStringProp(typInfo:TypeInfo,curObject:Any,key:String,value:String):Unit = {
      (for {
        filed <- typInfo.getFieldTry(key)
        value <- DynTypeConv.strConvertToTry(filed.typName,value)
       } yield filed.set(curObject,value)).logError();
    }

    def setXMLProp(typInfo:TypeInfo,key:String,curObject:Any,xml:XmlElement):Try[Unit] = Try {
      val fieldInfo = typInfo.getFieldTry(key).get;
      if(xml.children.length == 0 && xml.innerText.isDefined) {
          setStringProp(typInfo,curObject,key,xml.innerText.get);
      } else if(xml.children.length == 1) {
        val childValue = this.parse(xml).get;
        val convValue = DynTypeConv.convertStrTypeTry(childValue.getClass().getName(),fieldInfo.typName,childValue).get;
        fieldInfo.set(curObject,convValue);
      } else if(xml.children.length > 1) {
        val fieldObject = fieldInfo.get(curObject);
        setObjectList(fieldObject,xml);
      }
    }

    def setObjectList(curObject:Any,xml:XmlElement):Unit = {
      if(!curObject.isInstanceOf[Growable[_]]) {
        System.err.println(s"setObjectList: ${curObject} is not Growable");
        return;
      }
      val curGrowable = curObject.asInstanceOf[Growable[Any]];
      curGrowable.clear();
      for(childElem <- xml.children) {
        this.parse(childElem).logError().foreach(childObject => {
          curGrowable += childObject;
        })
      }
    }
}