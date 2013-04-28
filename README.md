[![Build Status](https://travis-ci.org/pathikrit/scalgos.png)](http://travis-ci.org/pathikrit/scalgos)

Goals
=====
0. Learn Scala
1. Text book implementation of common algorithms in idiomatic functional Scala
2. No external dependency (except specs2 for tests)
3. 100% test coverage and documentation

Building
========
0. Install scala (`brew install scala`) & [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)
1. Build and run tests: `sbt test` (in the project directory)
2. Importing into IntelliJ:
    0. Install scala & sbt plugin from plugin manager
    1. Save [this](https://github.com/yuanw/scalgos/blob/master/project/plugins.sbt) to `~/.sbt/plugins/build.sbt`
    2. Do `sbt gen-idea`
    3. "Open Project" and select the scalgos directory
