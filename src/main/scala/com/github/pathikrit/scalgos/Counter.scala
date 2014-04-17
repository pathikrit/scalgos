package com.github.pathikrit.scalgos

import collection.mutable

/**
 * A simple counter class that keeps count of objects
 * @tparam A type of item to count
 */
class Counter[A] extends mutable.Map[A, Int] {

  private val delegate = mutable.Map.empty[A, Int] withDefaultValue 0

  /**
   * Increment counter of kv._1 by kv._2
   */
  def +=(kv: (A, Int)): this.type = {
    delegate(kv._1) = delegate(kv._1) + kv._2
    this
  }

  /**
   * Decrement the counter of key by 1
   */
  def -=(key: A) = {
    delegate(key) = delegate(key) - 1
    this
  }

  /**
   * Get count of item
   * @param key count how many occurrences of key
   * @return Always returns Some(x) where x is occurrences of key.
   *         Some(0) is returned instead of None if key is not present or its count is zero
   */
  def get(key: A) = Some(delegate(key))

  def iterator = delegate.iterator

  /**
   * Count each item in items
   */
  def +=(items: A*): this.type = {
    items foreach {i => this += ((i, 1))}
    this
  }
}

/**
 * Companion object to Counter
 */
object Counter {

  /**
   * @return new empty counter
   */
  def empty[A] = new Counter[A]

  /**
   * Create new counter from given objects
   */
  def apply[A](items: A*) = {
    val c = Counter.empty[A]
    c += (items: _*)
    c
  }
}
