package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Randomized._

class RandomizedSpec extends Specification {

  "quickSelect" should {
    "match naive sort based algorithm" in {
      val s = RandomData.list()
      val k = RandomData.integer(0, s.length-1)
      quickSelect(s, k) must be equalTo s.sorted.apply(k)
    }

    "match median-of-medians algorithm" in todo
  }
}
