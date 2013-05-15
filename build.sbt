name := "scalgos"

version := "0.0.1"

scalaVersion := "2.10.1-RC1"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature",
  "-language:postfixOps", "-language:implicitConversions", "-language:experimental.macros"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.13" % "test",
  "org.scala-lang" % "scala-reflect" % "2.10.1-RC1"
)

