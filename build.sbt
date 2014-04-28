name := "scalgos"

version := "0.0.1"

scalaVersion := "2.10.4"

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.coverallsSettings

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature",
  "-language:postfixOps,implicitConversions,experimental.macros,dynamics,reflectiveCalls"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.specs2" %% "specs2" % "2.3.11" % "test"
)