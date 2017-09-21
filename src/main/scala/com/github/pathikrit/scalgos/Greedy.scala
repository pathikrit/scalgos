package com.github.pathikrit.scalgos

/**
 * Collection of greedy algorithms
 */
object Greedy {
  /**
   * Stack based solution to maximum rectangle in histogram problem
   * stack always has (h, x) such that h is in increasing order and x is the earliest index at which h can be spanned
   * O(n) - stack can be atmost size of remaining; no recursive step repeats previous; size of remaining never increases
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: List[Int]): Int = {
    def solve(stack: List[(Int, Int)], remaining: List[(Int, Int)]): Int = {
      def area(x: Int, y: Int) = (heights.length - remaining.length - x) * y
      (stack, remaining) match {
        case (           Nil,          Nil)           => 0
        case ((y, x) :: rest,          Nil)           => solve(          rest,    remaining) max area(x, y)
        case ((y, x) :: rest, (h, _) :: hs) if h <= y => solve(          rest, (h, x) :: hs) max area(x, y)
        case (             _,  block :: hs)           => solve(block :: stack,           hs)
      }
    }
    solve(Nil, heights.zipWithIndex)
  }
  
  /**
   * Merge a list of (possibly overlapping) closed-intervals into non-overlapping closed-intervals from left-to-right e.g.:
   * input: [(18, 19), (3, 9), (7, 10), (1, 5), (12, 17), (19, 21), (0, 6)]
   * output: [(0, 10), (12, 17), (18, 21)]
   */
  def mergeIntervals(intervals: (Int, Int)*): List[(Int, Int)] = {
    def solve(intervals: List[(Int, Int)]): List[(Int, Int)] = intervals match {
      case (x1, y1) :: (x2, y2) :: ps if y1 >= x2 => solve((x1, y1 max y2) :: ps)
      case p1 :: pt => p1 :: solve(pt)
      case _ => intervals
    }
    solve(intervals.toList.sorted)
  }
  //TODO: stable marraige
  //TODO: 2SAT
}
