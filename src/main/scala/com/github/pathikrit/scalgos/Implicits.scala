package scalgos

import collection.mutable

/**
 * Implicits that enhance default library stuff
 */
object Implicits {

  /**
   * Let's you use X instead of double for-loops
   */
  implicit class Crossable[X](xs: Traversable[X]) {
    def X[Y](ys: Traversable[Y]) = for ( x <- xs; y <- ys ) yield (x, y)
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
