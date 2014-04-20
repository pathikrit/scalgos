package com.github.pathikrit.scalgos

import collection.mutable.{Map => Dict}

/**
 * Generic way to create memoized functions (even recursive and multiple-arg ones)
 *
 * @param f the function to memoize
 * @tparam A input
 * @tparam B output
 */
case class Memo[A, B](f: A => B) extends (A => B) {
  private val cache = Dict.empty[A, B]
  override def apply(x: A) = cache getOrElseUpdate (x, f(x))
}

/**
 * Use this instead of Memo when we want to cache by a function of the input to f and not directly the input of f
 *
 * @tparam A1 input to f
 * @tparam A2 the keys we should use in cache instead of A1
 * @tparam B output of f
 */
case class FMemo[A1 <% A2, A2, B](f: A1 => B) extends (A1 => B) {
  private val cache = Dict.empty[A2, B]
  override def apply(x: A1) = cache getOrElseUpdate (x, f(x))
}

