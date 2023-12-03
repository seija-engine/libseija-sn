package com.seija.ui.xml
import scala.util.Try
import com.seija.ui.controls.UIElement
import com.seija.core.reflect.Assembly
import com.seija.core.reflect.{TypeInfo, FieldInfo}
import scala.util.Success
import com.seija.core.logError
import com.seija.ui.ContentProperty;
import com.seija.core.reflect.DynTypeConv
import com.seija.ui.binding.BindingItem
import com.seija.ui.ContentProperty
import scala.util.Failure
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Buffer
import com.seija.sxml.vm.{VMValue, XmlNode}

object XmlUIElement {
    def fromFile(filePath:String):Try[UIElement] = {
      val xmlElement = UISXmlEnv.evalFile(filePath).get.unwrap[VMValue.VMXml]().get.value
      fromXmlElement(xmlElement)
    }

    def fromXmlElement(xmlElem:XmlNode):Try[UIElement] = {
      val parser = SXmlObjectParser(XmlNSResolver.default);
      val curObject = parser.parse(xmlElem);
      val tryUIElement = curObject.map(_.asInstanceOf[UIElement])
      tryUIElement.map(_.Awake());
      tryUIElement
    }
}

case class NotFoundNSAlias(name:String) extends Exception;