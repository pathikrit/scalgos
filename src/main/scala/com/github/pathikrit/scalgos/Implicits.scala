package com.github.pathikrit.scalgos

import scala.collection.mutable

/**
 * Implicits that enhance default library stuff
 */
object Implicits { //TODO: Move to package.scala

  /**
   * Sometimes its convenient to map true to 1 and false to 0
   */
  implicit def toInt(x: Boolean): Int = if (x) 1 else 0

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
   * Extension to Booleans
   */
  implicit class BooleanExtensions(b: Boolean) {
    def then[A](f: => A): Option[A] = if (b) Some(f) else None
  }

  /**
   * Extensions to Ints
   */
  implicit class IntExtensions(x: Int) {
    /**
     * @return m = x mod y such that we preserve the relation (x/y)*y + m == x
     */
    def mod(y: Int) = x - (x/y)*y

    def ! = Combinatorics factorial x

    def `...` = Stream from x

    /**
     * @return range that goes forward or backward depending on x and y
     */
    def -->(y: Int) = x to y by (if (x < y) 1 else -1)

    /**
     * For doing 5 times f
     */
    def times[A](f: => A) = 1 to x map {i => f}
  }

  /**
   * Extensions to Longs
   */
  implicit class LongExtensions(x: Long) {
    /**
     * count set bits
     */
    def bitCount = java.lang.Long.bitCount(x)
  }

  implicit class TraversableExtension[A](t: Traversable[A]) {
    def firstDefined[B](f: A => Option[B]): Option[B] = t collectFirst Function.unlift(f)
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
  implicit class Invertible[K, V](map: Map[K, V]) {

    /**
     * Invert a map[K,V] to map[V, Iterable[K]]
     */
    def invert = map groupBy {_._2} mapValues {_ map (_._1)}
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

  /**
   * To get around the fact that indexOf returns -1 for missing instead of None.
   */
  def indexToOpt(idx: Int) = idx >= 0 then idx

  /**
   * @return If predicate is true, return Some(f) else None
   */
  def when[A](predicate: Boolean)(f: => A): Option[A] = predicate then f

  /**
   * Support some more operations on lists
   */
  implicit class RichList[A](l: List[A]) {

    /**
     * @return a list with items in position i and j swapped
     */
    def swap(i: Int, j: Int) = l.updated(i, l(j)).updated(j, l(i))

    /**
     * Remove 1 element
     *
     * @param elem element to remove
     * @return a list with first occurrence of elem removed
     */
    def -(elem: A): List[A] = l match {
      case Nil => Nil
      case x :: xs if x == elem => xs
      case x :: xs => x :: (xs - elem)
    }
  }

  /**
   * Let's us easily specify lazy streams that is a function of the last element
   */
  implicit class Streamer[A](start: A) {
    def `...`(f: A => A) = Stream.iterate(start)(f)
  }

  /**
   * F#'s forward pipe operator
   */
  implicit class PipedFunctions[A](x: => A) {
    def |>[B](f: A => B) = f(x)
  }
}
