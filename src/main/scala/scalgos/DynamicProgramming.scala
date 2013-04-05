package scalgos

import collection.mutable
import scala.math.Ordering.Implicits._

/**
 * Collection of DP algorithms
 */
object DynamicProgramming {

  /**
   * Generate all possible valid brackets
   * O(C(n)) = O(4^n / n^1.5)
   * Number of brackets = C(n) i.e. the n-th Catalan number
   * because C(n) = sigma(i = 0 to n-1 C(i)*C(n-i))
   *
   * @param n number of pairs
   * @return generate all possible valid n-pair bracket strings
   */
  def validBrackets(n: Int) = {
    val cache = mutable.Map.empty[Int, Seq[String]]

    def _validBrackets(n: Int): Seq[String] = cache getOrElseUpdate (n, if (n == 0) Seq("") else for {
      i <- 0 until n
      a <- _validBrackets(i)
      b <- _validBrackets(n-i-1)
    } yield '(' + a  + ')' + b)

    _validBrackets(n)
  }

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and width
   * O(n) since its basically recursive fibonacci algorithm
   * TODO: are there O(n) items in cache?
   *
   * @param dimensions (width, height)s of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(dimensions: Seq[(Int, Int)]) = {
    case class Block(width: Int, height: Int) {
      val area = width * height
      def join(other: Block) = Block(width + other.width, height min other.height)
    }
    implicit def toBlock(dimension: (Int, Int)) = Block(dimension._1, dimension._2)

    val cache = mutable.Map.empty[Seq[Block], Int]

    def area(blocks: Seq[Block]): Int = cache getOrElseUpdate (blocks, blocks match {
      case Nil => 0
      case first :: Nil => first.area
      case first :: second :: rest => Seq(first.area, area(second :: rest), area((first join second) :: rest)).max
    })

    area(dimensions map toBlock)
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
  def maxSubArraySum(s: Seq[Int]) = {
    var (current, best) = (0, 0)
    for (i <- s) {
      current = (current+i) max 0
      best = best max current
    }
    best
  }
}