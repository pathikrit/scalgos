package com.github.pathikrit.scalgos

import scala.util.Random
import scala.math.Ordering.Implicits._

/**
 * Collection of randomized algorithms
 */
object Randomized {

  /**
   * Randomized algorithm to select the kth item in a sequence in amortised linear time
   *
   * @return the k-th item in s
   */
 def quickSelect[A](s: Seq[A], k: Int)(implicit ord: Ordering[A]): A = {
    require(k >= 0 && k < s.size)
    val pivot = s((Math.random() * s.length).toInt)
    val table = s.groupBy(i => ord.compare(i, pivot)).withDefaultValue(Nil)
    val Seq(low, equal, high) = Seq(-1, 0, 1).map(table)
    if (k <= low.size) quickSelect(low, k)
    else if (k <= low.size + equal.size) pivot
    else quickSelect(high, k - low.size - equal.size)
  }

  /**
   * A Park-Miller Pseudo-random number generator (minimum standard one)
   *
   * @param i the seed e.g. use i = minstd(i)
   * @return a pseudo-random number
   */
  def minstd(i: Int) = (i*16807) % Int.MaxValue
}
