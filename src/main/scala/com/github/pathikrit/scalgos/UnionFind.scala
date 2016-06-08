package com.github.pathikrit.scalgos

import scala.collection.{generic, mutable}

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

  def union(x: A, y: A) = {
    // Randomized linking is O(an) too: http://www.cis.upenn.edu/~sanjeev/papers/soda14_disjoint_set_union.pdf
    // If input is randomized we don't need randomization anyway: http://codeforces.com/blog/entry/21476
    // Without any linking heuristics but only path compression, it is O(log n) too: http://stackoverflow.com/questions/2323351/
    if (scala.util.Random.nextBoolean()) parent(find(x)) = find(y) else parent(find(y)) = find(x)
  }
}
