package com.github.pathikrit.scalgos

import scala.collection.{mutable, generic}

/**
 * Simpler implementation of @see DisjointSet
 * TODO: test this with above
 */
class UnionFind[A] extends PartialFunction[A, A] with generic.Growable[A] {
  private[this] val parent = mutable.Map.empty[A, A]

  private[this] def find(x: A): A = parent(x) match {
    case `x` => x
    case y =>
      parent(x) = find(y)
      parent(x)
  }

  override def isDefinedAt(x: A) = parent contains x

  override def apply(x: A) = find(x)

  override def +=(x: A) = {
    parent(x) = x
    this
  }

  override def clear() = parent.clear()

  def union(x: A, y: A) = parent(find(x)) = find(y)
}
