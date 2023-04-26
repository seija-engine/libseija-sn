scalaVersion := "3.1.3"

enablePlugins(ScalaNativePlugin)
import scala.scalanative.build._



lazy val root = project
  .in(file("."))
  .settings(
    name := "libseija",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.1.3",
  )

nativeConfig ~= {
    _.withMode(Mode.debug)
    .withGC(GC.commix)
}