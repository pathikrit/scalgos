package com.github.pathikrit.scalgos

/**
 * Calculate prefix sums and support updates
 * @see http://codeforces.com/contest/635/submission/16433484
 */
trait BitIndexTree {
  /**
   * O(log n)
   * @return sum of elements in [0, i]
   */
  def prefixSum(i: Int): Int

  /**
   * O(log n)
   *
   * @return sum of all elements in [from, to]
   */
  def sum(from: Int, to: Int): Int = prefixSum(to) - prefixSum(from - 1)

  /**
   * Adds delta to the ith element
   * O(log n)
   */
  def +=(i: Int, delta: Int): Unit
}

class FenwickTree(n: Int) extends BitIndexTree {
  private[this] val tree = Array.ofDim[Int](n)

  override def prefixSum(i: Int) = if (i < 0) 0 else tree(i) + prefixSum((i & (i + 1)) - 1)

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
