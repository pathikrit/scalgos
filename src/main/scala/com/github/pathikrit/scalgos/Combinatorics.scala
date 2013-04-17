package com.github.pathikrit.scalgos

import scala.math.Ordering.Implicits._

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


  /**
   * Find next permutation of s
   * O(n)
   *
   * @return Some(p) if next permutation exists or None if s is already in decreasing order
   */
  def nextPermutation[A : Ordering](s: Seq[A]): Option[Seq[A]] = {
    val pivot = s zip s.tail lastIndexWhere {case (first, second) => first < second}
    if (pivot < 0) {
      None
    } else {
      val next = s lastIndexWhere {_ > s(pivot)}
      // swap the pivot and next, and then reverse the portion of the array to the right of where the pivot was found
      Some(((s take pivot) :+ s(next)) ++ ((s.slice(pivot+1, next):+ s(pivot)) ++ (s drop next+1)).reverse)
    }
  }

}
