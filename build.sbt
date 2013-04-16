name := "scalgos"

version := "0.0.1"

scalaVersion := "2.10.1-RC1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps", "-language:implicitConversions")

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.13" % "test"
)

