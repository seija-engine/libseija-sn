package ui.xml

import core.xml.XmlReader
import ui.Template
import scala.util.Try
import scala.util.Success
import scala.util.Failure


case class XmlTemplateReader(val reader:XmlReader) {
    def read():Try[Template] = {
        val event = reader.nextEvent().get;
        val paramName = event.castStart();
        if(paramName.isEmpty) {
            return Failure(Throwable(s"err tag:${event}"))
        }
        var template = Template();
        while(true) {
           val nextEvent = reader.lookNext().get;
           if(nextEvent.IsEnd(paramName.get) || nextEvent.IsEOF()) {
              reader.nextEvent();
              return Success(template)
           }
           val control = XmlControlReader(reader).read().get
           template.children = template.children :+ control;
        }
        Success(template)
    }
}