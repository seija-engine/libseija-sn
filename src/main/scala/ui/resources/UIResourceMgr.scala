package ui.resources
import core.logError;
import ui.xml.XmlObjectParser
import ui.xml.XmlNSResolver
import core.xml.XmlElement
object UIResourceMgr {
   var appResource:UIResource = UIResource.empty()


   def loadResource(xmlPath:String):Unit = {
       val xmlElement = XmlElement.fromFile(xmlPath).logError();
       if(xmlElement.isFailure) return;
       val parseObject = XmlObjectParser(XmlNSResolver.default).parse(xmlElement.get);
       parseObject.logError();
       if(parseObject.isFailure) return;
       val uiRes = parseObject.get.asInstanceOf[UIResource];
       for(res <- uiRes.resList) {
         this.appResource.addOne(res);
       } 
   }
}