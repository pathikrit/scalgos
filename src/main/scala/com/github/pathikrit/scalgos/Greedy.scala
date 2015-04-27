package com.github.pathikrit.scalgos

/**
 * Collection of greedy algorithms
 */
object Greedy {

  /**
   * Stack based solution to maximum rectangle in histogram problem
   * stack always has (h, x) such that h is increasing order x is the earliest index at which h can be spanned
   * O(n)
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: Seq[Int]): Int = {
    val (_, ans) = (heights :+ 0).zipWithIndex.foldLeft((List.empty[(Int, Int)], 0)) {
      case ((stack, best), (height, index)) =>
        val (taller, shorter) = stack.span(_._1 > height)
        val newBest = taller.foldLeft(best){case (a, (y, x)) => a max (y * (index - x))}
        val pos = taller.lastOption.map(_._2) getOrElse index
        ((height -> pos) :: shorter, newBest)
    }
    ans
  }
}

