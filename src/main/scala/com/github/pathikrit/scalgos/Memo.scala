package com.github.pathikrit.scalgos

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam A input
 * @tparam B output
 */
case class Memo[A, B](f: A => B) extends (A => B) {
  import collection.mutable.{Map => Dict}
  private val cache = Dict.empty[A, B]
  override def apply(x: A) = cache getOrElseUpdate (x, f(x))
}
