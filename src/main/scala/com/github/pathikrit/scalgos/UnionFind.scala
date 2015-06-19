package com.github.pathikrit.scalgos

import scala.collection.mutable

import Implicits.when

/**
 * Simpler implementation of @see DisjointSet
 * TODO: test this with above
 */
class UnionFind[A] {
  private[this] val parent = mutable.Map.empty[A, A]

  private[this] def find(x: A): A = parent(x) match {
    case `x` => x
    case y =>
      parent(x) = find(y)
      parent(x)
  }

  def apply(x: A) = when(contains(x))(find(x))

  def union(x: A, y: A) = parent(find(x)) = find(y)

  def +=(x: A) = parent(x) = x

  def contains(x: A) = parent contains x
}
