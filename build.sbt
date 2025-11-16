name := "misc-scala-utils"

organization := "com.github.dzufferey"

version := "1.1.1-SNAPSHOT"

scalaVersion := "3.3.7"

crossScalaVersions := Seq("2.13.17", "3.3.7")

libraryDependencies ++=  Seq(
    "org.scalatest" %% "scalatest" % "3.2.19" % "test"
)

// needed for scala 2 macros
libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    case _ => Seq.empty
  }
}


scalacOptions := Seq("-unchecked", "-deprecation")

