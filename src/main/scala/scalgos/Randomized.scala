package scalgos

import util.Random.{nextInt, nextDouble}
import scala.math.Ordering.Implicits._

/**
 * Collection of randomized algorithms
 */
object Randomized {

  /**
   * @return Random Integer in [@param start, @param end]
   */
  def randomInteger(start: Int = 0, end: Int) = {
    assume(end > start)
    start + nextInt(end - start + 1)
  }

  /**
   * @return Random Double in [@param start, @param end]
   */
  def randomNumber(start: Double = 0, end: Double) = {
    assume(end > start)
    start + (end - start)*nextDouble()
  }

  /**
   * Randomized algorithm to select the kth item in a sequence in amortised linear time
   *
   * @param s input sequence
   * @param k
   * @return the k-th item in s
   */
  def quickSelect[T: Ordering](s: Seq[T], k: Int): T = {
    assume(k >= 0 && k < s.size)
    val pivot = s(nextInt(s.length))
    val (low, rest) = s partition {_ < pivot}
    if (k < low.size) {
      quickSelect(low, k)
    } else {
      val (equal, high) = rest partition {_ == pivot}
      if (k < low.size + equal.size) pivot else quickSelect(high, k - low.size - equal.size)
    }
  }
}
