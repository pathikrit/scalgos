package com.github.pathikrit.scalgos

import collection.mutable

/**
 * Implicits that enhance default library stuff
 */
object Implicits {

  /**
   * Sometimes its convenient to map true to 1 and false to 0
   */
  implicit def toInt(x: Boolean) = if (x) 1 else 0

  /**
   * Better floating point comparison with a tolerance of eps = 1e-9
   * @param x treats x as a range [x-eps, x+eps]
   */
  implicit class FuzzyDouble(x: Double)(implicit eps: Double = 1e-9) {

    /**
     * @return true iff x > y+eps
     */
    def >~(y: Double) = x > y+eps

    /**
     * @return true iff x >= y-eps
     */
    def >=~(y: Double) = x >= y-eps

    /**
     * @return true iff x < y-eps
     */
    def ~<(y: Double) = x < y-eps

    /**
     * @return true iff x <= y+eps
     */
    def ~=<(y: Double) = x <= y+eps

    /**
     * @return true iff x in [y-eps, y+eps]
     */
    def ~=(y: Double) = ~=<(y) && >=~(y)
  }

  /**
   * Extensions to Ints
   */
  implicit class IntExtensions(x: Int) {
    /**
     * @return m = x mod y such that we preserve the relation (x/y)*y + m == x
     */
    def mod(y: Int) = x - (x/y)*y

    def ! = Combinatorics.factorial(x)
  }

  /**
   * Let's you use X instead of double for-loops
   */
  implicit class Crossable[A](as: Traversable[A]) {
    def X[B](bs: Traversable[B]) = for {a <- as; b <- bs} yield (a, b)
  }

  /**
   * Supports map inversions
   */
  implicit class Invertible[K,V](map: collection.Map[K, V]) {

    /**
     * Invert a map[K,V] to map[V, Iterable[K]]
     */
    def invert = map groupBy (_._2) mapValues {_ map (_._1)}
  }

  /**
   * Supports priority updates and quick remove mins
   */
  implicit class Updateable[A](queue: mutable.TreeSet[A]) {
    /**
     * Remove and return the smallest value from a TreeSet queue
     * O(log n)
     */
    def removeFirst = {
      val head = queue.head
      queue -= head
      head
    }

    /**
     * Hack to update priority of a node by deleting and re-adding
     * O (log n)
     *
     * @param node node whose priority is being updated
     * @param update a function given a node updates its priority (is called for given node)
     */
    def updatePriority(node: A, update: A => Unit) {
      queue -= node
      update(node)
      queue += node
    }
  }
}
