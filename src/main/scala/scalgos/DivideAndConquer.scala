package scalgos

object DivideAndConquer {

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and unit width
   * Basically creates min-heap with smallest height at root plus its left and right
   * O(n * depth-of-heap)
   * A faster O(n) worst case DP algorithm exists
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: Seq[Int]): Int = if (heights isEmpty) 0 else {
    val (left, smallest :: right) = heights splitAt (heights indexOf heights.min)
    Seq(maxRectangleInHistogram(left), smallest * heights.length, maxRectangleInHistogram(right)).max
  }
}
