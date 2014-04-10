resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-scoverage" % "0.95.7")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")
