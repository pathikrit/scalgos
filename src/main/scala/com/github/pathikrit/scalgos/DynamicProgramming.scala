package com.github.pathikrit.scalgos

import scala.math.Ordering.Implicits._
import scala.collection.mutable

import Implicits._
import Memo._

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
  def isSubsetSumAchievable(s: List[Int], t: Int) = {
    type DP = Memo[(List[Int], Int), (Int, Int), Boolean]
    implicit def encode(key: (List[Int], Int)) = (key._1.length, key._2)

    lazy val f: DP = Memo {
      case (_, 0) => true         // 0 can always be achieved using empty list
      case (Nil, _) => false      // we can never achieve non-zero if we have empty list
      case (a :: as, x) => f(as, x - a) || f(as, x)      // try with/without a.head
    }

    f(s, t)
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
  def subsetSum(s: List[Int], t: Int) = {
    type DP = Memo[(List[Int], Int), (Int, Int), Seq[Seq[Int]]]
    implicit def encode(key: (List[Int], Int)) = (key._1.length, key._2)

    lazy val f: DP = Memo {
      case (Nil, 0) => Seq(Nil)
      case (Nil, _) => Nil
      case (a :: as, x) => f(as, x - a).map(_ :+ a) ++ f(as, x)
    }

    f(s, t)
  }

  /**
   * Partition a sequence into two partitions such that difference of their sum is minimum
   * O(s.length * s.sum)
   *
   * @param s list to partition
   * @return a partition of s into a and b s.t. |a.sum - b.sum| is minimum
   */
  def closestPartition(s: List[Int]) = {
    type DP = Memo[(List[Int], Int), (Int, Int), Option[Seq[Int]]]
    implicit def encode(key: (List[Int], Int)) = (key._1.length, key._2)

    lazy val f: DP = Memo {
      case (_, 0) => Some(Nil)
      case (Nil, _) => None
      case (a :: as, x) => f(as, x - a).map(_ :+ a) orElse f(as, x)
    }

    val possible = f(s, _: Int)                 // check if _ can be created using all elements of s
    (s.sum/2 --> 0 firstDefined possible).get   // find largest such x < s.sum/2 (always a solution at 0)
  }

  /**
   * KnapSack problem
   */
  object KnapSack {

    /**
     * @param value value of this item - more the better
     * @param weight weight of this item - less the better
     */
    case class Item(value: Int, weight: Int) {
      require(weight > 0)
    }

    /**
     * O(maxWeight * items.length)
     * 0-1 knapsack problem
     * @return sublist of items such that total weight < maxWeight and sum of values is maximized
     */
    def apply(items: List[Item], maxWeight: Int): List[Item] = {
      require(maxWeight >= 0)

      type DP = Memo[(List[Item], Int), (Int, Int), (Int, List[Item])]
      implicit def encode(key: (List[Item], Int)) = (key._1.length, key._2)

      lazy val f: DP = Memo {
        case (i :: is, w) if w >= i.weight =>
          val ((v1, c1), (v2, c2)) = (f(is, w), f(is, w - i.weight))
          if (v2 + i.value > v1) (v2 + i.value, i :: c2) else (v1, c1)

        case (i :: is, w) if w > 0 => f(is, w)

        case _ => (0, Nil)
      }

      f(items, maxWeight)._2
    }

    /**
     * Generalized version of the problem
     * O(?)
     *
     * @param items counts of available items (multiple copies of the item can be available)
     * @param maxCount maximum number of items we can select
     * @param maxWeight maximum weight we can use
     * @return subCounter of items such that total weight < maxWeight, count < maxCount and sum of values is maximized
     */
    def apply(items: Counter[Item], maxCount: Int, maxWeight: Int): Counter[Item] = ???
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
  def editDistance[A](s1: Seq[A], s2: Seq[A], delete: Int = 1, insert: Int = 1, replace: Int = 1) = {
    assume(delete > 0 && insert > 0 && replace > 0)

    type DP = Memo[(Seq[A], Seq[A]), (Int, Int), Int]
    implicit def encode(key: (Seq[A], Seq[A])) = (key._1.length, key._2.length)

    lazy val f: DP = Memo {
      case (a, Nil) => a.length * (delete min insert)
      case (Nil, b) => b.length * (delete min insert)
      case (a :: as, b :: bs) if a == b => f(as, bs)
      case (a, b) => (delete + f(a, b.tail)) min (insert + f(a.tail, b)) min (replace + f(a.tail, b.tail))
    }

    f(s1, s2)
  }

  /**
   * Generate all possible valid brackets
   * O(C(n)) = O(4^n / n^1.5)
   * Number of brackets = C(n) i.e. the n-th Catalan number
   * because C(n) = sigma(i = 0 to n-1 C(i)*C(n-i))
   *
   * @return memoized function to generate all possible valid n-pair bracket strings
   */
  val validBrackets: Int ==> IndexedSeq[String] = Memo {
    case 0 => IndexedSeq("")
    case n => for {
      i <- 0 until n
      (a, b) <- validBrackets(i) X validBrackets(n-i-1)
    } yield s"($a)$b"
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
  def longestCommonSubsequence[A](a: List[A], b: List[A]) = {
    type DP = Memo[(List[A], List[A]), (Int, Int), List[A]]
    implicit def encode(key: (List[A], List[A])) = (key._1.length, key._2.length)

    implicit val c: Ordering[List[A]] = Ordering by {_.length}

    lazy val f: DP = Memo {
      case (Nil, _) | (_, Nil) => Nil
      case (x :: xs, y :: ys) if x == y => x :: f(xs, ys)
      case (x, y) => c.max(f(x.tail, y), f(x, y.tail))
    }

    f(a, b)
  }

  /**
   * Find longest (strictly) increasing subsequence
   * O(n log n)
   * Proof of correctness by induction
   *
   * @param s input sequence
   * @return return longest increasing subsequence of a
   */
  def longestIncreasingSubsequence[A: Ordering](s: Seq[A]) = {
    val cache = mutable.Map(0 -> Seq.empty[A]) // cache(i) is longest increasing sequence of length i
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
    def findCandidate(a: A, start: Int = 0, end: Int = longest): Int = {
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
   * @return  (max, min) such that
   *          max(i) =  largest sum achievable from first i elements
   *          min(i) = smallest sum achievable from first i elements
   */
  def bounds(s: Seq[Int]) = {
    def f(comp: (Int, Int) => Int) = s.scanLeft(0){(sum, i) => comp(sum + i, sum)}
    val order = Ordering[Int]
    (f(order.max), f(order.min))
  }

  /**
   * Find the maximum sum of a contiguous sub array
   * O(n) Kadane's algorithm
   *
   * @param s
   * @return the maximum contiguous sub array sum
   */
  def maxSubArraySum(s: Seq[Int]) = s.scanLeft(0){_ + _ max 0}.max
}
