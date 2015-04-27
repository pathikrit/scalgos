package com.github.pathikrit.scalgos

/**
 * Collection of greedy algorithms
 */
object Greedy {

  /**
   * Stack based solution to maximum rectangle in histogram problem
   * stack always has (h, x) such that h is increasing order x is the earliest index at which h can be spanned
   * O(n) - TODO: Proof?
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: List[Int]): Int = {
    def solve(stack: List[(Int, Int)], remaining: List[(Int, Int)]): Int = {
      def area(y: Int, x: Int) = y * (heights.length - remaining.length - x)
      (stack, remaining) match {
        case (           Nil,          Nil)           => 0
        case ((y, x) :: rest,          Nil)           => solve(          rest,    remaining) max area(y, x)
        case ((y, x) :: rest, (h, _) :: hs) if h <= y => solve(          rest, (h, x) :: hs) max area(y, x)
        case (             _,  block :: hs)           => solve(block :: stack,           hs)
      }
    }
    solve(Nil, heights.zipWithIndex)
  }
}
