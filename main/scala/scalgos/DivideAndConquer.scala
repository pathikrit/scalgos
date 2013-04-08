package scalgos.sandbox

object DivideAndConquer {

  /**
   * Find area of largest rectangle under histogram
   *
   * O(n log n) average case algorithm (O(n*n) worst case)
   * Basically recursively builds min-heap
   * (with left containing all heights left of smallest and right containing all heights greater than smallest)
   * Faster O(n) worst case DP algorithm exists
   *
   * @param heights heights of bar
   * @return largest rectangle under histogram
   */
  def maxRectangleUnderHistogram(heights: Seq[Int]): Int = if (heights isEmpty) 0 else {
    val (left, smallest :: right) = heights splitAt (heights indexOf heights.min)
    Seq(maxRectangleUnderHistogram(left), smallest * heights.length, maxRectangleUnderHistogram(right)).max
  }


}
