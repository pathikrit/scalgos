[![Build Status](https://travis-ci.org/pathikrit/scalgos.png)](http://travis-ci.org/pathikrit/scalgos)

Goals
=====
1. Text book implementation of common algorithms in idiomatic functional Scala
2. No external dependency (except specs2 for tests)
3. 100% test coverage and documentation

Building
========
1. Install [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)
2. Build and run tests: `sbt test`
3. Importing into IntelliJ:
    1. Install scala & sbt plugin
    2. Do `sbt gen-idea`
    3. "Open Project" and select the scalgos directory
