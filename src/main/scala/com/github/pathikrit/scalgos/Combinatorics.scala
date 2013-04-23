package com.github.pathikrit.scalgos

import math.Ordering.Implicits._

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

  /**
   * @return memoized function to calculate n!
   */
  val factorial: Memo[Int, BigInt] = Memo {n => if (n == 0) 1 else n * factorial(n-1)}

  /**
   * Fibonacci number calculator
   * O(n) - each number is calculated once in O(1) time
   *
   * @return memoized function to calculate nth fibonacci number
   */
  val fibonacci: Memo[Int, BigInt] = Memo {n => if (n <= 1) n else fibonacci(n-1) + fibonacci(n-2)}

  /**
   * Calculate catalan number
   * O(n*n) - each recursive step takes O(n) time
   * A faster relation exists : c(n) = (4n+2)*c(n-1)/(n+2)
   *
   * @return memoized function to calculate nth catalan number
   */
  val catalan: Memo[Int, BigInt] = Memo {n => if (n == 0) 1 else (0 until n) map {i => catalan(i) * catalan(n-i-1)} sum}
}
