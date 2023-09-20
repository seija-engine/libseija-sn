scalaVersion := "3.3.0"
enablePlugins(ScalaNativePlugin)
import scala.scalanative.build._



lazy val root = project
  .in(file("."))
  .settings(
    name := "libseija",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.3.0",
  )

nativeConfig ~= {
    _.withMode(Mode.debug)
}

libraryDependencies +=  "com.lihaoyi" %%% "sourcecode" % "0.3.0"