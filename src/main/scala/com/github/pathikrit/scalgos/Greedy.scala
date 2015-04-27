package com.github.pathikrit.scalgos

/**
 * Collection of greedy algorithms
 */
object Greedy {

  /**
   * Stack based solution to maximum rectangle in histogram problem
   * stack always has (h, x) such that h is increasing order x is the earliest index at which h can be spanned
   * O(n) - TODO: Proof?
   * TODO: Move the longer rule based solution to gist and move its test into greedy/divide and conquer
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: List[Int]): Int = {
    def solve(stack: List[(Int, Int)], remaining: List[(Int, Int)]): Int = (stack, remaining) match {
      case (Nil, Nil) => 0
      case ((y, x) :: rest, Nil) => solve(rest, remaining) max (y*(heights.length - x))
      case (_, (h, i) :: hs) =>
        val (taller, shorter) = stack.span(_._1 > h)
        val pos = taller.lastOption.map(_._2) getOrElse i
        val best = solve((h, pos) :: shorter, hs)
        taller.foldLeft(best){case (a, (y, x)) => a max (y * (i - x))}
    }
    solve(Nil, heights.zipWithIndex)
  }
}
