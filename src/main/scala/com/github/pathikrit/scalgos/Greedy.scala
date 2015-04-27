package com.github.pathikrit.scalgos

/**
 * Collection of greedy algorithms
 */
object Greedy {
  /**
   * Stack based solution to maximum rectangle in histogram problem
   * stack always has (h, x) such that h is increasing order x is the earliest index at which h can be spanned
   * O(n) - Each recursive call reduces length of stack + remaining by one
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: List[Int]): Int = {
    def solve(stack: List[(Int, Int)], remaining: List[(Int, Int)]): Int = (stack, remaining) match {
      case (           Nil,          Nil)           => 0
      case ((y, x) :: rest,          Nil)           => solve(rest, remaining) max (y * (heights.length - x))
      case ((y, x) :: rest, (h, i) :: hs) if y >= h => solve(rest, (h, x) :: hs) max (y * (i - x))
      case (             _, (h, i) :: hs)           => solve((h, i) :: stack, hs)
    }
    solve(Nil, heights.zipWithIndex)
  }
}
