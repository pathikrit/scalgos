name := "scalgos"

version := "0.0.1"

scalaVersion := "2.11.2"

instrumentSettings

CoverallsPlugin.coverallsSettings

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.specs2" %% "specs2" % "2.4.1" % "test"
)
