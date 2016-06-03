package com.github.pathikrit.scalgos

import scala.reflect.ClassTag

/**
  * Segment tree: A data structure that lets you quickly do fold on an interval
  */
trait SegmentTree[A] extends (Int => A) {
  def update(idx: Int, value: A): Unit
  def fold(z: A)(from: Int, to: Int): A
}

object SegmentTree {
  /**
    * @see http://codeforces.com/blog/entry/18051
    * O(n) to construct
    * O(1) for apply
    * O(log n) for update and fold
    *
    * @tparam A
    */
  def apply[A: ClassTag](data: Traversable[A])(f: (A, A) => A) = new SegmentTree[A] {
    private[this] val n = data.size
    private[this] val t = Array.ofDim[A](2 * n)

    private[this] def build(i: Int) = t(i) = f(t(2*i), t(2*i + 1))

    data.copyToArray(t, n)
    (n - 1 until 0 by -1) foreach build

    override def apply(idx: Int) = t(idx + n)

    override def update(idx: Int, value: A) = {
      var p = idx + n
      t(p) = value
      while (p > 1) {
        build(p/2)
        p /= 2
      }
    }

    override def fold(z: A)(from: Int, to: Int) = {
      var result = z
      var (l, r) = (from + n, to + n)
      while (l <= r) {
        if (l%2 == 1) result = f(result, t(l))
        if (r%2 == 0) result = f(result, t(r))
        l = (l + 1)/2
        r = (r - 1)/2
      }
      result
    }
  }

  def naive[A: ClassTag](data: Traversable[A])(f: (A, A) => A) = new SegmentTree[A] {
    private[this] val array: Array[A] = data.toArray
    override def apply(i: Int) = array(i)
    override def update(idx: Int, value: A) = array(idx) = value
    override def fold(z: A)(from: Int, to: Int) = array.slice(from, to+1).foldLeft(z)(f)
  }
}
