package com.github.pathikrit.scalgos

import org.specs2.mutable._

class OverlappingIntervalSpec extends Specification {

  "merge" should {
    "merge" in {
      val actual = OverlappingIntervals.merge((18, 19), (3, 9), (7, 10), (1, 5), (12, 17), (19, 21), (0, 6))
      val expected = List((0, 10), (12, 17), (18, 21))
      actual shouldEqual expected
    }
  }
}
