package com.github.pathikrit.scalgos

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam I input to f
 * @tparam K the keys we should use in cache instead of I
 * @tparam O output of f
 */
case class Memo[I <% K, K, O](f: I => O) extends (I => O) {
  import collection.mutable.{Map => Dict}
  val cache = Dict.empty[K, O]
  override def apply(x: I) = cache getOrElseUpdate (x, f(x))
}

object Memo {
  /**
   * Type of a simple memoized function e.g. when I = K
   */
  type F[I, O] = Memo[I, I, O]
}
