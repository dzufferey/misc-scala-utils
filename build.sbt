name := "misc-scala-utils"

organization := "com.github.dzufferey"

version := "1.1.0"

scalaVersion := "3.1.0"

crossScalaVersions := Seq("2.13.7", "3.1.0")

libraryDependencies ++=  Seq(
    "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)

// needed for scala 2 macros
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    case _ => Seq.empty
  }
}


scalacOptions := Seq("-unchecked", "-deprecation")

