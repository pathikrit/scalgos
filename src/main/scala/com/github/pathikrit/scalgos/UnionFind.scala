package com.github.pathikrit.scalgos

import scala.collection.generic.Clearable
import scala.collection.mutable

/**
  * Simpler implementation of @see DisjointSet
  */
class UnionFind[A] extends PartialFunction[A, A] with Clearable {
  private[this] val parent = mutable.Map.empty[A, A].withDefault(identity)

  private[this] def find(x: A): A = parent(x) match {
    case `x` => x
    case y =>
      parent(x) = find(y)
      parent(x)
  }

  override def isDefinedAt(x: A) = parent.contains(x)

  override def apply(x: A) = find(x)

  def toMap: Map[A, A] = (parent.keySet ++ parent.values).map(u => u -> find(u)).toMap

  def sets: Map[A, Iterable[A]] = parent.keys.groupBy(find)

  def union(x: A, y: A): this.type = {
    // Randomized linking is O(a(n)) too: http://www.cis.upenn.edu/~sanjeev/papers/soda14_disjoint_set_union.pdf
    // If input is randomized we don't need randomization anyway: http://codeforces.com/blog/entry/21476
    // Without any linking heuristics but only path compression, it is O(log n) too: http://stackoverflow.com/questions/2323351/
    if (scala.util.Random.nextBoolean()) parent(find(x)) = find(y) else parent(find(y)) = find(x)
    this
  }

  override def clear() = parent.clear()
}

object UnionFind {
  def apply[A](edges: Traversable[(A, A)]): UnionFind[A] =
    edges.foldLeft(new UnionFind[A]){case (state, (u, v)) => state.union(u, v)}
}