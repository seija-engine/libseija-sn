package ui.xml
import scala.util.Try
import core.xml.XmlElement
import core.reflect.TypeInfo
import core.reflect.DynTypeConv
import core.logError;
import scala.collection.mutable.Growable
import ui.ContentProperty
import ui.controls.UIElement
import ui.binding.BindingItem
import scala.util.Success
import scala.collection.mutable.ArrayBuffer
import ui.resources.Style
import ui.resources.UIResourceMgr

class XmlObjectParser(val nsResolver: XmlNSResolver = XmlNSResolver.default) {

    def parse(xml: XmlElement): Try[Any] = Try {
      xml.name match
        case "string" | "String" => xml.innerText.getOrElse("")
        case _ => this._parse(xml).get
    }

    def _parse(xml: XmlElement): Try[Any] = Try {
    
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
      
      val contentList:Option[Growable[Any]] = contentField.flatMap { f =>
        val fObject = f.get(curObject)
        if(fObject.isInstanceOf[Growable[_]]) {
          Some(fObject.asInstanceOf[Growable[Any]])
        } else { None }
      }
      if(contentField.isDefined) {
        val curElement = if(curObject.isInstanceOf[UIElement]) { Some(curObject.asInstanceOf[UIElement]) } else { None }
        if(contentCount == 0 && xml.innerText.isDefined) {
          setStringProp(curTypeInfo,curObject,contentField.get.Name,xml.innerText.get);
        } else if(contentCount == 1) {
          xml.children.filter(_.name.indexOf(".") < 0).foreach { childElem =>
            val childObject = this.parse(childElem).logError();
            if(childObject.isSuccess) {
              val ctxObject = childObject.get;
              if(contentList.isDefined) {
                contentList.get += ctxObject;
                curElement.foreach(_.onAddContent(ctxObject));
              } else {
                  val convValue = DynTypeConv.convertStrTypeTry(ctxObject.getClass().getName(),contentField.get.typName,ctxObject);
                  if(convValue.logError().isSuccess) {
                    contentField.get.set(curObject,convValue.get);
                    curElement.foreach(_.onAddContent(convValue.get));
                  }
              }
            }
          }
        } else if(contentCount > 1) {
          if(contentList.isDefined) {
              for(childElem <- xml.children.filter(_.name.indexOf(".") < 0)) {
                  val childObject = this.parse(childElem).logError();
                  if(childObject.isSuccess) {
                    contentList.get += childObject.get;
                    curElement.foreach(_.onAddContent(childObject.get));
                  }
              }
          }
        }

        
      }
      curObject
    }

    def setStringProp(typInfo:TypeInfo,curObject:Any,key:String,value:String):Unit = {
      val isUIElement = curObject.isInstanceOf[UIElement];
      if (isUIElement && value.startsWith("{Binding")) {
        BindingItem.parse(key, value).logError().map(curObject.asInstanceOf[UIElement].addBindItem(_))
      } else if(value.startsWith("{Res")) {
        val startLen = "{Res".length();
        var styleName = value.substring(startLen,value.length() - 1);
        styleName = styleName.trim();
        if(curObject.isInstanceOf[UIElement]) {
          val uiElement = curObject.asInstanceOf[UIElement];
          var findStyle = uiElement.findResourceStyle(styleName);
          if(findStyle.isEmpty) { findStyle = UIResourceMgr.appResource.findStyle(styleName); }
          uiElement.setStyle(findStyle);
        } else {
          UIResourceMgr.appResource.findStyle(styleName).foreach {style =>  
             typInfo.getField(key).foreach(f => f.set(curObject,style));
          }
        }
      } else {
        (for {
          filed <- typInfo.getFieldTry(key)
          value <- DynTypeConv.strConvertToTry(filed.typName,value)
        } yield filed.set(curObject,value)).logError();
      }
    }

    def setXMLProp(typInfo:TypeInfo,key:String,curObject:Any,xml:XmlElement):Try[Unit] = Try {
      val fieldInfo = typInfo.getFieldTry(key).get;
      var fieldList:Option[Growable[Any]] =  None;
      val fObject = fieldInfo.get(curObject)
      if(fObject.isInstanceOf[Growable[_]]) {
         fieldList = Some(fObject.asInstanceOf[Growable[Any]])
      }

      if(xml.children.length == 0 && xml.innerText.isDefined) {
          setStringProp(typInfo,curObject,key,xml.innerText.get);
      } else if(fieldList.isDefined) {
        setObjectList(fObject,xml);
      } else {
        val childValue = this.parse(xml.children(0)).get;
        val convValue = DynTypeConv.convertStrTypeTry(childValue.getClass().getName(),fieldInfo.typName,childValue).get;
        fieldInfo.set(curObject,convValue);
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