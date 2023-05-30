package ui.xml
import core.xml.XmlElement
import scala.util.Try
import ui.controls.UIElement
import core.reflect.Assembly
import core.reflect.{TypeInfo,FieldInfo}
import scala.util.Success
import core.logError
import ui.ContentProperty;
import core.reflect.DynTypeConv
import ui.binding.BindingItem
import ui.ContentProperty
import scala.util.Failure
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Buffer

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
      val xmlElement = XmlElement.fromFile(filePath).get;
      fromXmlElement(xmlElement)
    }

    def fromXmlElement(xmlElem:XmlElement):Try[UIElement] = {
      val curObject = XmlObjectParser(XmlNSResolver.default).parse(xmlElem);
      curObject.map(_.asInstanceOf[UIElement])
    }
    /*
    def parseXMLObject(xmlElem:XmlElement):Try[Any] = Try {
      val typInfo = this.getType(xmlElem.name).get;
      val newObject = typInfo.create();
      setObjectStringProps(typInfo,newObject,xmlElem);
      val contentPropName = typInfo.getAnnotation[ContentProperty].map(_.name);
      val fieldContent = contentPropName.flatMap(typInfo.getField(_));
      val lstObject:Option[Buffer[Any]] = if(fieldContent.isDefined) {
        val filedObject = fieldContent.get.get(newObject);
        if(filedObject.isInstanceOf[Buffer[_]]) {Some(filedObject.asInstanceOf[Buffer[Any]])} else { None }
      } else { None }
      
      //xml attr
      for(childElem <- xmlElem.children) {
        if(childElem.name.indexOf('.') > 0) {
           this.setXMLProp(newObject,typInfo,childElem).logError();
        } else {
           if(lstObject.isDefined) {
              this.parseXMLObject(childElem).logError().foreach {v =>
                //TODO Check Type?
                lstObject.get.addOne(v);
              }
           } else {
              this.parseXMLObject(childElem).logError().foreach {v => 
                fieldContent.foreach(f => f.set(newObject,v))
              }
           }
        }
      }
      newObject
    }

    def setXMLProp(curObject:Any,typInfo:TypeInfo,childElem:XmlElement):Try[Unit] = Try {
      val filedNames = childElem.name.split('.');
      val fieldInfo:FieldInfo = typInfo.getFieldTry(filedNames(1)).get;
      
      childElem.children.length match {
        case 0 => {
          if(childElem.innerText.isDefined) {
            this.trySetValue(curObject,fieldInfo,childElem.innerText.get).logError();
          }
        }
        case 1 => {
          val propValue = this.parseXMLObject(childElem.children(0)).logError();
          propValue.foreach(v => this.trySetValue(curObject,fieldInfo,v).logError())
          
        }
        case _ => {
           val curFieldObject = fieldInfo.get(curObject);
           val filedTypeInfo = Assembly.getTypeInfoOrThrow(curFieldObject);
           val lstObject = this.tryGetListObject(filedTypeInfo,curFieldObject);
           if(lstObject.isDefined) {
             for(propChildElem <- childElem.children) {
                this.parseXMLObject(propChildElem).logError().foreach {v =>
                  //TODO Check Type?
                  lstObject.get.addOne(v);
                }
             }
           }
        }
      }
    }

    def setObjectStringProps(typInfo:TypeInfo,curObject:Any,xmlElem:XmlElement) = {
      val isUIElement = curObject.isInstanceOf[UIElement];
      for((k,v) <- xmlElem.attributes) {
         val tryErr:Try[Unit] = if (isUIElement && v.startsWith("{Binding")) {
            BindingItem.parse(k, v).map(curObject.asInstanceOf[UIElement].addBindItem(_))
          } else {
            for {
            field <- typInfo.getFieldTry(k)
            convValue <- DynTypeConv.strConvertToTry(field.typName,v)
            } yield { field.set(curObject,convValue); }
          }
          tryErr.logError();
      }
    }

    def getType(xmlName:String):Try[TypeInfo] = {
      val fullTypeName = XmlReadSetting.default.toFullName(xmlName);
      if(fullTypeName.isEmpty) {
        return Failure(NotFoundNSAlias(xmlName))
      }
      Assembly.getTry(fullTypeName.get)
    }

    def trySetValue(curObject:Any,fieldInfo:FieldInfo,fromValue:Any):Try[Unit] = Try {
      val convValue = DynTypeConv.convertStrTypeTry(fromValue.getClass().getName(),fieldInfo.typName,fromValue).get;
      fieldInfo.set(curObject,convValue);
    }

    def tryGetListObject(typInfo:TypeInfo,curObject:Any):Option[Buffer[Any]] = {
      val contentPropName = typInfo.getAnnotation[ContentProperty].map(_.name);
      val fieldContent = contentPropName.flatMap(typInfo.getField(_));
      if(fieldContent.isDefined) {
        val filedObject = fieldContent.get.get(curObject);
        if(filedObject.isInstanceOf[Buffer[_]]) {Some(filedObject.asInstanceOf[Buffer[Any]])} else { None }
      } else { None }
    }*/
}

case class NotFoundNSAlias(name:String) extends Exception;