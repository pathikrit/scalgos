package com.github.pathikrit.scalgos

import scala.collection.mutable

object OverlappingIntervals {
  type Interval = (Int, Int)

  /**
    * @return
    *   A data structure that supports O(1) addition of an interval
    *   and then O(1) querying of number of overlaps over any point
    */
  def countOverlaps(intervals: TraversableOnce[Interval]): IndexedSeq[Int] = {
    val ends = mutable.IndexedSeq.empty[Int]
    intervals foreach { case (from, to) =>
      ends(from) += 1
      ends(to) -= 1
    }
    ends.scanLeft(0)(_ + _)
  }

  /**
    * Merge a list of (possibly overlapping) closed-intervals into non-overlapping closed-intervals from left-to-right e.g.:
    * input: [(18, 19), (3, 9), (7, 10), (1, 5), (12, 17), (19, 21), (0, 6)]
    * output: [(0, 10), (12, 17), (18, 21)]
    */
  def merge(intervals: Interval*): List[Interval] = {
    def solve(intervals: List[Interval]): List[Interval] = intervals match {
      case (x1, y1) :: (x2, y2) :: ps if y1 >= x2 => solve((x1, y1 max y2) :: ps)
      case p1 :: pt => p1 :: solve(pt)
      case _ => intervals
    }
    solve(intervals.toList.sorted)
  }
}
