package com.github.pathikrit.scalgos

import math.Ordering.Implicits._
import collection.mutable

import Implicits._

/**
 * Generic way to create memoized functions (even recursive ones)
 *
 * @param f the function to memoize
 * @tparam A input
 * @tparam B output
 */
case class Memo[A,B](f: A => B) extends (A => B) {
  private val cache = mutable.Map.empty[A, B]
  def apply(x: A) = cache getOrElseUpdate (x, f(x))
}

/**
 * Collection of DP algorithms
 */
object DynamicProgramming {

  /**
   * Subset sum algorithm - can we achieve sum t using elements from s?
   * O(s.map(abs).sum * s.length)
   *
   * @param s set of integers
   * @param t target
   * @return true iff there exists a subset of s that sums to t
   */
  def subsetSum(s: Seq[Int], t: Int): Boolean = {
    val max = s.scanLeft(0)((sum, i) => (sum + i) max sum)
    val min = s.scanLeft(0)((sum, i) => (sum + i) min sum)

    val cache = mutable.Map.empty[(Int, Int), Boolean]

    def dp(i: Int, x: Int): Boolean = (i, x) match {
      case (_, 0) => true
      case (0, _) => false
      case _ => cache getOrElseUpdate ((i, x), min(i) <= x && x <= max(i) && (dp(i-1, x - s(i-1)) || dp(i-1, x)))
    }

    dp(s.length, t)
  }


  /**
   * Generate all possible valid brackets
   * O(C(n)) = O(4^n / n^1.5)
   * Number of brackets = C(n) i.e. the n-th Catalan number
   * because C(n) = sigma(i = 0 to n-1 C(i)*C(n-i))
   *
   * @return memoized function to generate all possible valid n-pair bracket strings
   */
  val validBrackets: Memo[Int, Seq[String]] = Memo {
    case 0 => Seq("")
    case n => for {
      i <- 0 until n
      (a,b) <- validBrackets(i) X validBrackets(n-i-1)
    } yield '(' + a  + ')' + b
  }

  /**
   * Find longest common subsequence (not necessarily contiguous) of 2 sequences
   * O(a.length * b.length) since each item in cache is filled exactly once in O(1) time
   *
   * @param a first sequence
   * @param b second sequence
   * @return longest common subsequence of a and b
   */
  def longestCommonSubsequence[T](a: Seq[T], b: Seq[T]) = {
    val (x, y) = (a.length, b.length)
    val lcs = Array.ofDim[Seq[T]](x+1,y+1)

    for (i <- 0 to x; j <- 0 to y) lcs(i)(j) = {
      if (i == 0 || j == 0)
        Nil
      else if (a(i-1) == b(j-1))
        lcs(i-1)(j-1) :+ a(i-1)
      else if (lcs(i-1)(j).length > lcs(i)(j-1).length)
        lcs(i-1)(j)
      else
        lcs(i)(j-1)
    }
    lcs(x)(y)
  }

  /**
   * Find longest (strictly) increasing subsequence
   * O(n log n)
   * Proof of correctness by induction
   *
   * @param s input sequence
   * @return return longest increasing subsequence of a
   */
  def longestIncreasingSubsequence[T: Ordering](s: Seq[T]) = {
    val best = mutable.Map.empty[Int, Seq[T]] // best(i) is longest sequence of length i
    best(0) = Nil

    /**
     * Find i such that (best(i) :: a) is a valid increasing sequence where start <= i <= end
     * O(log n) since we binary search
     * TODO: use the DivideAndConquer.binarySearch
     *
     * @param a element to be inserted
     * @param start start index of best
     * @param end end index of best
     * @return the longest item from best[start..end] where a can be appended to
     */
    def findCandidate(a: T, start: Int = 0, end: Int = best.size - 1): Int = {
      if (start == end) {
        start
      } else {
        assert(end > start)
        val mid = (start + end + 1)/2       // bias towards right to handle 0,1 case since best(0).last is invalid
        if (best(mid).last < a) {
          findCandidate(a, mid, end)
        } else {
          findCandidate(a, start, mid-1)
        }
      }
    }

    for (item <- s) {
      // Fredman-Knuth speedup: Quickly check if we can extend current best before doing binary search
      val position = if (best.size > 1 && best(best.size-1).last < item) best.size-1 else findCandidate(item)
      best(position+1) = best(position) :+ item   // end element of smaller list < end elements of larger lists
    }

    best(best.size - 1)
  }

  /**
   * Find the maximum sum of a contiguous sub array
   * O(n) Kadane's algorithm
   *
   * @param s
   * @return the maximum contiguous sub array sum
   */
  def maxSubArraySum(s: Seq[Int]) = s.scanLeft(0)((sum, i) => (sum + i) max 0).max
}