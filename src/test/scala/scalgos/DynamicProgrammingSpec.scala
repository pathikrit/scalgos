package scalgos

import org.specs2.mutable._

import scalgos.DynamicProgramming._

class DynamicProgrammingSpec extends Specification {

  "maxRectangleUnderHistogram" should {
    "return 0 for empty histograms" in { maxRectangleInHistogram(Nil) must be equalTo(0) }

    "work for arbitrary input" in {
      val dims = Seq((6, 1), (3, 5), (8, 1), (4, 9), (5, 3), (8, 2), (1, 18), (2, 2), (19, 1), (2, 10))
      maxRectangleInHistogram(dims) must be equalTo 58
    }
  }
}
