package com.github.pathikrit.scalgos

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam I1 input to f
 * @tparam I2 the keys we should use in cache instead of I1
 * @tparam O output of f
 */
case class Memo[I1 <% I2, I2, O](f: I1 => O) extends (I1 => O) {
  import collection.mutable.{Map => Dict}
  val cache = Dict.empty[I2, O]
  override def apply(x: I1) = cache getOrElseUpdate (x, f(x))
}

object Memo {
  /**
   * Type of a simple memoized function e.g. when I1 = I2
   */
  type F[I, O] = Memo[I, I, O]
}
