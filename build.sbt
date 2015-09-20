name := "scalgos"
version := "1.0-SNAPSHOT"
description := "Algorithms in Scala"
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
organization := "com.github.pathikrit"

scalaVersion := "2.11.7"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:_")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.specs2" %% "specs2" % "2.4.1" % Test
)
