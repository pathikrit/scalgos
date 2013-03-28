package scalgos

import org.specs2.mutable._
import scalgos.DivideAndConquer._

class DivideAndConquerSpec extends Specification {

  "maxRectangleUnderHistogram" should {
    "return 0 for empty histograms" in { maxRectangleUnderHistogram(Nil) must be equalTo(0) }

    "work for arbitrary input" in {
      val heights = Seq(2, 7, 9, 11, 1, 1, 18, 6, 8, 5)
      maxRectangleUnderHistogram(heights) must be equalTo 21
    }

    "return same result as the DP algorithm" in {
      val heights = Seq(2, 7, 9, 11, 1, 1, 18, 6, 8, 5)
      maxRectangleUnderHistogram(heights) must be equalTo scalgos.DynamicProgramming.maxRectangleUnderHistogram(heights)
    }
  }

}
