package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

class DisjointSetSpec extends Specification {

  "DisjointSet" should {
    "work" in {
      val ds = DisjointSet(1 to 6: _*)
      (ds += 1) must throwA[AssertionError]
      ds union (2,3)
      ds union (4,5)
      ds union (5,6)
      ds union (0,6) must throwA[AssertionError]
      ds(1) must be equalTo 1
      ds(2) must be equalTo 2
      ds(3) must be equalTo 2
      ds(4) must be equalTo 4
      ds(5) must be equalTo 4
      ds(6) must be equalTo 4
      ds(7) must throwA[AssertionError]
      ds.sets must containTheSameElementsAs(Seq(Set(1), Set(2,3), Set(4,5,6)))
    }
  }
}
