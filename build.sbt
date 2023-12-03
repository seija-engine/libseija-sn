scalaVersion := "3.3.0"
enablePlugins(ScalaNativePlugin)
import scala.scalanative.build._

ThisBuild / organization := "org.seija"

name := "libseija"


nativeConfig ~= {
    _.withMode(Mode.debug)
}



//.withBuildTarget(BuildTarget.libraryStatic)
libraryDependencies +=  "com.lihaoyi" %%% "sourcecode" % "0.3.0"