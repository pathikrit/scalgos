package com.github.pathikrit.scalgos

import math.Ordering.Implicits._
import collection.mutable

import Implicits._

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam A input
 * @tparam B output
 */
case class Memo[A, B](f: A => B) extends (A => B) {
  import collection.mutable.{Map => Dict}
  private val cache = Dict.empty[A, B]
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
  def isSubsetSumAchievable(s: IndexedSeq[Int], t: Int) = {
    val (max, min) = bounds(s)

    lazy val dp: Memo[(Int, Int), Boolean] = Memo {         // dp(i,x) = can we achieve x using the first i elements?
      case (_, 0) => true                                   // 0 can always be achieved using empty set
      case (i, x) if x < min(i) || max(i) < x => false      // outside range
      case (i, x) => dp(i-1, x - s(i-1)) || dp(i-1, x)      // try with/without s(i-1)
    }

    dp(s.length, t)
  }

  /**
   * Subset sum algorithm - How can we achieve sum t using elements from s?
   * O(s.map(abs).sum * s.length)
   * Can be modified to for simple without replacement coin change problem
   *
   * @param s set of integers
   * @param t target
   * @return all subsets of s that sum to t
   */
  def subsetSum(s: IndexedSeq[Int], t: Int) = {
    val (max, min) = bounds(s)

    lazy val dp: Memo[(Int, Int), Seq[Seq[Int]]] = Memo {
      case (0, 0) => Seq(Nil)
      case (i, x) if x < min(i) || max(i) < x => Nil
      case (i, x) => (dp(i-1, x - s(i-1)) map {_ :+ s(i-1)}) ++ dp(i-1, x)
    }

    dp(s.length, t)
  }

  /**
   * @return  (max, min) such that
   *          max(i) =  largest sum achievable from first i elements
   *          min(i) = smallest sum achievable from first i elements
   */
  def bounds(s: Seq[Int]) = {
    val max = s.scanLeft(0){(sum, i) => (sum + i) max sum}
    val min = s.scanLeft(0){(sum, i) => (sum + i) min sum}
    (max, min)
  }

  /**
   * Calculate edit distance between 2 sequences
   * O(s1.length * s2.length)
   *
   * @param delete cost of delete operation
   * @param insert cost of insert operation
   * @param replace cost of replace operation
   * @return Minimum cost to convert s1 into s2 using delete, insert and replace operations
   */
  def editDistance[A](s1: IndexedSeq[A], s2: IndexedSeq[A], delete: Int = 1, insert: Int = 1, replace: Int = 1) = {
    lazy val dp: Memo[(Int, Int), Int] = Memo {   // dp(a,b) = edit distance of s1.substring(0,a) and s2.substring(0,b)
      case (a, 0) => a * (delete min insert)
      case (0, b) => b * (delete min insert)
      case (a, b) if s1(s1.length - a) == s2(s2.length - b) => dp(a-1, b-1)
      case (a, b) => (delete + dp(a, b-1)) min (insert + dp(a-1, b)) min (replace + dp(a-1, b-1))
    }

    dp(s1.length, s2.length)
  }

  /**
   * Generate all possible valid brackets
   * O(C(n)) = O(4^n / n^1.5)
   * Number of brackets = C(n) i.e. the n-th Catalan number
   * because C(n) = sigma(i = 0 to n-1 C(i)*C(n-i))
   *
   * @return memoized function to generate all possible valid n-pair bracket strings
   */
  val validBrackets: Memo[Int, IndexedSeq[String]] = Memo {
    case 0 => IndexedSeq("")
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
   * @return a longest common subsequence of a and b
   *         if multiple possible lcs, return the one that is "earliest" in a
   */
  def longestCommonSubsequence[T](a: IndexedSeq[T], b: IndexedSeq[T]) = {
    val c = Ordering by {s: Seq[T] => s.length}
    lazy val dp: Memo[(Int, Int), Seq[T]] = Memo {    //dp(x,y) - lcs of a[0..x) and b[0..y)
      case (_, 0) => Nil
      case (0, _) => Nil
      case (x, y) if a(x-1) == b(y-1) => dp(x-1, y-1) :+ a(x-1)
      case (x, y) => c.max(dp(x-1, y), dp(x, y-1))
    }
    dp(a.length, b.length)
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
    val cache = mutable.Map(0 -> Seq.empty[T]) // cache(i) is longest increasing sequence of length i
    def longest = cache.size - 1

    /**
     * Find i such that (cache(i) :: a) is a valid increasing sequence where start <= i <= end
     * O(log n) since we binary search
     * TODO: use the DivideAndConquer.binarySearch
     *
     * @param a element to be inserted
     * @param start start index of best
     * @param end end index of best
     * @return the longest item from best[start..end] where a can be appended to
     */
    def findCandidate(a: T, start: Int = 0, end: Int = longest): Int = {
      if (start == end) {
        start
      } else {
        assert(end > start)
        val mid = (start + end + 1)/2       // bias towards right to handle 0,1 case since best(0).last is invalid
        if (cache(mid).last < a) findCandidate(a, mid, end) else findCandidate(a, start, mid-1)
      }
    }

    for (item <- s) {
      // Fredman-Knuth speedup: Quickly check if we can extend current best before doing binary search
      val position = if (cache.size > 1 && cache(longest).last < item) longest else findCandidate(item)
      cache(position+1) = cache(position) :+ item   // end element of smaller list < end elements of larger lists
    }

    cache(longest)
  }

  /**
   * Find the maximum sum of a contiguous sub array
   * O(n) Kadane's algorithm
   *
   * @param s
   * @return the maximum contiguous sub array sum
   */
  def maxSubArraySum(s: Seq[Int]) = s.scanLeft(0){(sum, i) => (sum + i) max 0}.max
}
