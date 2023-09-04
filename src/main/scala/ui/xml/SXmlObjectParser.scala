package ui.xml
import core.logError
import core.reflect.{DynTypeConv, FieldInfo, TypeInfo}
import sxml.vm.{VMValue, XmlNode}
import ui.ContentProperty
import ui.binding.BindingItem
import ui.controls.UIElement
import ui.resources.UIResourceMgr

import scala.collection.mutable
import scala.collection.mutable.Growable
import scala.util.Try

trait IXmlObject {
    def OnAddContent(value:Any):Unit;
}


class SXmlObjectParser(val nsResolver: XmlNSResolver = XmlNSResolver.default) {
  def parse(xml: XmlNode): Try[Any] = Try {
    println(xml)
    xml.Name match
      case "string" | "String" => xml.child(0).toScalaValue()
      case _                   => this._parse(xml).get
  }

  def _parse(xml: XmlNode): Try[Any] = Try {
    val curTypeInfo = nsResolver.resolverTypeInfo(xml.Name).get
    val curObject = curTypeInfo.create()
    //设置attr里的属性
    for ((k, v) <- xml.attrs) {
      setObjectProp(curTypeInfo, curObject, k, v.toScalaValue()).logError()
    }
    //取出当前对象的内容字段,如果是List转换为List
    val contentName = curTypeInfo.getAnnotation[ContentProperty].map(_.name).getOrElse("content")
    val contentField = curTypeInfo.getField(contentName)
    val contentList: Option[mutable.Growable[Any]] = contentField.flatMap { f =>
      val fObject = f.get(curObject)
      if (fObject.isInstanceOf[mutable.Growable[_]]) {
        Some(fObject.asInstanceOf[mutable.Growable[Any]])
      } else { None }
    }
    //添加子元素
    for(childElem <- xml.child) {
      childElem match
        case VMValue.VMXml(value) => {
          if(value.Name.indexOf('.') > 0) {
            this.setXMLProp(curTypeInfo,curObject,value)
          } else {
            contentField.foreach {v => this._addObjectContent(v,contentList,value,curObject) }
          }
        }
        case  _ => contentField.foreach { v => this._addObjectContent(v,contentList,childElem.toScalaValue(),curObject) }
    }

    curObject
  }

  def _addObjectContent(contentField:FieldInfo, contentList:Option[mutable.Growable[Any]], value:Any,curObject:Any):Unit = {
    val curElement = if(curObject.isInstanceOf[IXmlObject]) { Some(curObject.asInstanceOf[IXmlObject]) } else { None }
    val childObject = value match
      case xmlValue: XmlNode => this.parse(xmlValue).get
      case _ => value
    contentList match
      case Some(ctxList) => {
        println(s"add chilren:${childObject}")
        ctxList += childObject
        curElement.foreach(_.OnAddContent(childObject))
      }
      case None => {
        val convValue = DynTypeConv.convertStrTypeTry(childObject.getClass.getName,contentField.typName,childObject)
        if (convValue.logError().isSuccess) {
          contentField.set(curObject, convValue.get);
          curElement.foreach(_.OnAddContent(convValue.get));
        }
      }
  }

  def setObjectProp(typInfo: TypeInfo, curObject: Any, key: String, value: Any): Try[Unit] = Try {
    val isUIElement = curObject.isInstanceOf[UIElement]
    val attrValue: Any = value
    val setAnyValue = () => {
      (for {
        filed <- typInfo.getFieldTry(key)
        value <- DynTypeConv.convertStrTypeTry(value.getClass.getName,filed.typName, attrValue)
      } yield filed.set(curObject, value)).get
    }
    attrValue match
      case stringValue:String => {
        if(isUIElement && stringValue.startsWith("{Binding")) {
          val curUIElement = curObject.asInstanceOf[UIElement]
          curUIElement.addBindItem(BindingItem.parse(key, stringValue).get)
        } else if(stringValue.startsWith("{Res")) {
          val startLen = "{Res".length()
          var resName = stringValue.substring(startLen, stringValue.length - 1)
          resName = resName.trim()
          this.setRes(resName, typInfo, key, curObject)
        } else { setAnyValue() }
      }
      case _ => setAnyValue()
  }

  def setXMLProp(typInfo: TypeInfo,curObject:Any,xmlNode:XmlNode):Try[Unit] = Try {

  }

  def setRes(resName: String,typeInfo: TypeInfo,key: String,curObject: Any): Unit = {
    UIResourceMgr.appResource.findRes(resName).foreach { res =>
      typeInfo.getField(key).foreach { f =>
        DynTypeConv
          .convertStrTypeTry(res.getClass.getName, f.typName, res)
          .foreach { v =>
            f.set(curObject, v)
          }
      }
    }
  }

}
