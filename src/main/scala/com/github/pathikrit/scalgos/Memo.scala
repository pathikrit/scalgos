package com.github.pathikrit.scalgos

import collection.mutable.{Map => Dict}

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam A1 input to f
 * @tparam A2 the keys we should use in cache instead of A1
 * @tparam B output of f
 */
case class Memo[A1 <% A2, A2, B](f: A1 => B) extends (A1 => B) {
  val cache = Dict.empty[A2, B]
  override def apply(x: A1) = cache getOrElseUpdate (x, f(x))
}

object Memo {

  /**
   * Type of a simple memoized function
   */
  type F[A, B] = Memo[A, A, B]
}
