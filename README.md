[![CircleCI][circleCiImg]][circleCiLink] [![codecov][codecovImg]][codecovLink] [![Codacy][codacyImg]][codacyLink]

Goals
=====
* [Learn Scala](http://stackoverflow.com/tags/scala/info)
* Text book [implementations](src/main/scala/com/github/pathikrit/scalgos) of common algorithms in idiomatic functional Scala
* No external [dependencies](build.sbt) (except [specs2](http://etorreborre.github.io/specs2/) for tests)
* Good [tests](src/test/scala/com/github/pathikrit/scalgos) and documentation

Building
========
* Install git, scala and sbt: `brew install git scala sbt`
* Clone project: `git clone https://github.com/pathikrit/scalgos.git; cd scalgos`
* Build and run tests: `sbt test`


[circleCiImg]: https://img.shields.io/circleci/project/pathikrit/scalgos/master.svg
[circleCiLink]: https://circleci.com/gh/pathikrit/scalgos

[codecovImg]: https://img.shields.io/codecov/c/github/pathikrit/scalgos/master.svg
[codecovLink]: http://codecov.io/github/pathikrit/scalgos?branch=master

[codacyImg]: https://img.shields.io/codacy/7628da9b32734c1c96b55b5650aa96be.svg
[codacyLink]: https://www.codacy.com/app/pathikritscalgos/dashboard
