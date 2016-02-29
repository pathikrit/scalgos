package com.github.pathikrit.scalgos

/**
 * Calculate prefix sums and support updates
 * @see http://codeforces.com/contest/635/submission/16423034
 */
trait BitIndexTree {
  /**
   * O(log n)
   * @return sum of elements in [0, i]
   */
  def apply(i: Int): Int

  /**
   * O(log n)
   *
   * @return sum of all elements in [start, end)
   */
  def apply(start: Int, end: Int): Int = this(end) - this(start - 1)

  /**
   * Adds delta to the ith element
   * O(log n)
   */
  def +=(i: Int, delta: Int): Unit
}

class FenwickTree(n: Int) extends BitIndexTree {
  private[this] val tree = Array.ofDim[Int](n)

  override def apply(i: Int) = if (i < 0) 0 else tree(i) + apply((i & (i + 1)) - 1)

  override def +=(i: Int, delta: Int) = if (i < n) {
    tree(i) += delta
    this += (i | (i + 1), delta)
  }
}

class UpdateableFenwickTree(n: Int) extends FenwickTree(n) {
  private[this] val data = Array.ofDim[Int](n)

  def update(i: Int, value: Int) = {
    this += (i, value - data(i))
    data(i) = value
  }
}
