name := "misc-scala-utils"

organization := "io.github.dzufferey"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.3"

crossScalaVersions := Seq("2.12.12", "2.13.3")

libraryDependencies ++=  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)

scalacOptions := Seq("-unchecked", "-deprecation")

//addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")

publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

pomExtra :=
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>

