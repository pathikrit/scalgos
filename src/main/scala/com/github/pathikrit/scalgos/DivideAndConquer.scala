package com.github.pathikrit.scalgos

import math.Ordered._

import Implicits.FuzzyDouble

/**
 * Collection of divide and conquer algorithms
 */
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
    maxRectangleInHistogram(left) max smallest * heights.length max maxRectangleInHistogram(right)
  }

  /**
   * Generic binary search in [min,max] f to achieve target goal
   * O(log n)
   *
   * @param f the function to binary search over - must be monotonically increasing
   * @param min starting minimum guess
   * @param max starting maximum guess
   * @param avg mid function usually (min+max)/2
   * @param goal target to achieve
   * @tparam A input type of f
   * @tparam B output type of f
   * @return x such that f(x) is as close to goal as possible
   */
  def binarySearch[A: Ordering, B: Ordering](f: A => B, min: A, max: A, avg: (A, A) => A, goal: B): A = {
    val mid = avg(min, max)
    if (min < mid && mid < max) {
      f(mid) compare goal match {
        case  1 => binarySearch(f, min, mid, avg, goal)
        case -1 => binarySearch(f, mid, max, avg, goal)
        case  0 => mid
      }
    } else {
      mid
    }
  }

  /**
   * Ternary search for maxima/minima of f in (left,right)
   * O (log n)
   * f must be U (or upside-down U) between left and right
   *
   * @param max true if search for maxima i.e. f is U else false
   * @return x such that f(x) is maximum (or minimum) in f assuming f is unimodal on [left,right]
   */
  def ternarySearch[A: Ordering](left: Double, right: Double, f: Double => A, max: Boolean = true): Double = {
    assume(right >~ left)
    val (l, r) = ((2*left+right)/3, (left+2*right)/3)
    if (l ~= r) {
      (l+r)/2 		// sometimes good idea (l-delta) to (r+delta) minBy/maxBy f
    } else if (f(l) > f(r) ^ max) {
      ternarySearch(l, right, f)
    } else {
      ternarySearch(left, r, f)
    }
  }
}
