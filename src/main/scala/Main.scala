import scalanative.unsigned.UnsignedRichInt
import scala.scalanative.unsafe
import scala.scalanative.runtime.libc
import core.CoreModule
import window.WindowModule



type Vec = unsafe.CStruct3[Float, Float, Float]

object Main {
  def main(args: Array[String]): Unit = {
    val app = core.App();
    app.addModule(CoreModule());
    app.addModule(WindowModule())


    app.start();
    app.run();
  }

  
}




