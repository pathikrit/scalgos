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
   * O(n) - Each step in collapse decreased either sorted.length or unprocessed.length and each step is O(1)
   *
   * @param dimensions (height, width)s of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(dimensions: Seq[(Int, Int)]) = {
    case class Block(height: Int, width: Int) {
      val area = width * height
      def +(that: Block) = Block(this.height min that.height, this.width + that.width)
    }

    implicit val sizeOrdering: Ordering[Block] = Ordering by {_.area}

    /**
     * Find the largest area block by recursively collapsing unprocessed into an increasing stack
     * Before calling make sure sorted has the (0,0) block
     *
     * @param sorted blocks in increasing order of height
     *
     * @param unprocessed the unprocessed part of the input
     *                    if empty, start collapsing the sorted part
     *                    else take head and see if head > sorted.top (always exists since we inserted (0,0) before)
     *                    if head > sorted.top, push head
     *                    else if sorted.top > head > sorted.second, combine sorted.head and head
     *                    else combine sorted.top and sorted.second
     *
     * @return the largest block
     */
    def collapse(sorted: List[Block], unprocessed: Seq[Block]): Block = unprocessed match {
      case Nil => sorted match {
        case first :: Nil => first
        case first :: second :: stack => first max collapse((first + second) :: stack, unprocessed)
      }
      case (current :: rest) => sorted match {
        case first :: stack if current.height >= first.height => collapse(current :: sorted, rest)

        case first :: second :: stack if current.height >= second.height =>
          first max collapse((first + current) :: second :: stack, rest)

        case first :: second :: stack => first max collapse((first + second) :: stack, unprocessed)
      }
    }

    collapse(List(Block(0, 0)), dimensions map {d => Block(d._1, d._2)}).area
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
  def maxSubArraySum(s: Seq[Int]) = s.scanLeft(0)((sum, i) => (sum + i) max 0).max
}