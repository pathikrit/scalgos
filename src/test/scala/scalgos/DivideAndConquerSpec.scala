package scalgos

import org.specs2.mutable._

import scalgos.DivideAndConquer._

class DivideAndConquerSpec extends Specification {

  "maxRectangleUnderHistogram" should {
    "return 0 for empty histograms" in { maxRectangleInHistogram(Nil) must be equalTo(0) }

    "work for arbitrary input" in {
      val heights = Seq(6, 3, 8, 4, 5, 8, 1, 2, 9, 2)
      maxRectangleInHistogram(heights) must be equalTo 18
    }

    "return same result as the DP algorithm" in {
      import Randomizations.randomInteger
      val heights = Seq.fill(100)(randomInteger(1, 100))
      maxRectangleInHistogram(heights) must be equalTo DynamicProgramming.maxRectangleInHistogram(heights map {(1, _)})
    }
  }
}
