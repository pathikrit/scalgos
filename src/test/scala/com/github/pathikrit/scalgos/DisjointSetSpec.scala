package scalgos

import org.specs2.mutable._

class DisjointSetSpec extends Specification {

  "DisjointSet" should {
    "work" in {
      val ds = new DisjointSet[Int]()
      (1 to 6) foreach ds.makeSet
      ds makeSet 1 must throwA[AssertionError]
      ds union (2,3)
      ds union (4,5)
      ds union (5,6)
      ds union (0,6) must throwA[AssertionError]
      ds find 1 must be equalTo 1
      ds find 2 must be equalTo 2
      ds find 3 must be equalTo 2
      ds find 4 must be equalTo 4
      ds find 5 must be equalTo 4
      ds find 6 must be equalTo 4
      ds find 7 must throwA[AssertionError]
      ds.sets must containTheSameElementsAs(Seq(Set(1), Set(2,3), Set(4,5,6)))
    }
  }
}
