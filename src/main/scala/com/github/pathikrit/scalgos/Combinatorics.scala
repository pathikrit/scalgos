package com.github.pathikrit.scalgos

/**
 * collection of algorithms related to combinatorics
 */
object Combinatorics {

  /**
   * Iterate over all 2^n combinations
   *
   * @param s sequence to do combination over
   * @param f applied to each possible combination
   * @return result of applied to
   */
  def combinations[A,B](s: Seq[A], f: Seq[A] => B) = for {i <- 0 to s.length; j <- s combinations i} yield f(j)

}
