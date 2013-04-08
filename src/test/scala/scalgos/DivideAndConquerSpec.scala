package scalgos

import scalgos.DivideAndConquer._

class DivideAndConquerSpec extends RandomData {

  "maxRectangleUnderHistogram" should {
    "be 0 for empty histograms" in {
      maxRectangleInHistogram(Nil) must be equalTo(0)
    }

    "work for arbitrary input" in {
      val heights = Seq(6, 3, 8, 4, 5, 8, 1, 2, 9, 2)
      maxRectangleInHistogram(heights) must be equalTo 18
    }

    "be same result as the DP algorithm" in {
      val heights = randomPositiveSeq()
      maxRectangleInHistogram(heights) must be equalTo DynamicProgramming.maxRectangleInHistogram(heights map {(1, _)})
    }
  }
}
