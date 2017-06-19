package com.github.pathikrit.scalgos

import scala.collection.mutable

/**
  * A data structure that supports O(1) addition of an interval
  * and then O(1) querying of number of overlaps over any point
  */
object OverlappingIntervals {
  case class Interval(from: Int, to: Int, count: Int = 1)

  def apply(intervals: TraversableOnce[Interval]): IndexedSeq[Int] = {
    val ends = mutable.IndexedSeq.empty[Int]
    intervals foreach { case Interval(from, to, count) =>
      ends(from) += count
      ends(to) -= count
    }
    ends.scanLeft(0)(_ + _)
  }
}
