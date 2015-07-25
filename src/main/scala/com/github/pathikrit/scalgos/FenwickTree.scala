package com.github.pathikrit.scalgos

/**
 * Calculate prefix sums and support updates
 * @param n size of the tree
 */
class FenwickTree(n: Int) {
  private[this] val tree = Array.ofDim[Int](n)

  /**
   * O(log n)
   * @return sum of the first i elements
   */
  def apply(i: Int): Int = if (i > 0) tree(i) + apply(i - (i & -i)) else 0

  /**
   * Set the ith element (1 index) to v
   * O(log n)
   */
  def update(i: Int, v: Int): Unit = if (i < n) {
    tree(i) += v
    this(i + (i & -i)) = v
  }
}
