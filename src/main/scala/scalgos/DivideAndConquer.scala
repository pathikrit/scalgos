package scalgos

object DivideAndConquer {

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and unit width
   * O(n log n) average case (worst case O(n*n)
   * A faster O(n) worst case DP algorithm exists
   * Basically creates min-heap with smallest height at root and its left and right
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleUnderHistogram(heights: Seq[Int]): Int = if (heights isEmpty) 0 else {
    val (left, smallest :: right) = heights splitAt (heights indexOf heights.min)
    Seq(maxRectangleUnderHistogram(left), smallest * heights.length, maxRectangleUnderHistogram(right)).max
  }

}
