package scalgos

import collection.mutable

/**
 * Random utility methods
 */
object Utils {

  /**
   * Invert a map[K,V] to map[V, Iterable[K]]
   */
  def invert[K, V](map: collection.Map[K, V]) = map groupBy (_._2) mapValues {_ map (_._1)}

  /**
   * Remove and return the smallest value from a TreeSet queue
   */
  def removeFirst[A](queue: mutable.TreeSet[A]) = {
    val head = queue.head
    queue -= head
    head
  }

  /**
   * Hack to update priority of a node by deleting and re-adding
   */
  def updatePriority[A](queue: mutable.TreeSet[A], node: A, update: A => Unit) {
    queue -= node
    update(node)
    queue += node
  }
}
