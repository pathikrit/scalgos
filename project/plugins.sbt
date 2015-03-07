resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.0.3")
