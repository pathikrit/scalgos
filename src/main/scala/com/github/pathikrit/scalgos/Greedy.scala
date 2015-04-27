package com.github.pathikrit.scalgos

import scala.annotation.tailrec

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
  def maxRectangleInHistogram(heights: List[Int]): Int = {
    @tailrec
    def solve(stack: List[(Int, Int)], best: Int, remaining: List[(Int, Int)]): Int = (stack, remaining) match {
      case (Nil, Nil) => best
      case ((y, x) :: ss, Nil) => solve(ss, best max (y*(heights.length - x)), remaining)
      case (_, (h, i) :: hs) =>
        val (taller, shorter) = stack.span(_._1 > h)
        val newBest = taller.foldLeft(best){case (a, (y, x)) => a max (y * (i - x))}
        val pos = taller.lastOption.map(_._2) getOrElse i
        solve((h, pos) :: shorter, newBest, hs)
    }
    solve(Nil, 0, heights.zipWithIndex)
  }
}
