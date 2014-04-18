resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.5")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.98.2")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")
