package scalgos

import org.specs2.mutable._

import scalgos.DynamicProgramming._

class DynamicProgrammingSpec extends Specification {

  "maxRectangleUnderHistogram" should {
    "return 0 for empty histograms" in { maxRectangleInHistogram(Nil) must be equalTo(0) }

    "work for arbitrary input" in {
      val heights = Seq(6, 3, 8, 4, 5, 8, 1, 2, 19, 2)
      maxRectangleInHistogram(heights) must be equalTo 19
    }
  }
}
