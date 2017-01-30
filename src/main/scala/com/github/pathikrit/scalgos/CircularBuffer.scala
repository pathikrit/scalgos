package com.github.pathikrit.scalgos

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * A data structure that provides O(1) get, update, length, append, prepend, clear, trimStart and trimRight
  * @tparam A
  */
class CircularBuffer[A: ClassTag](initialSize: Int = 1<<4) extends mutable.Buffer[A] {
  private var array = Array.ofDim[A](initialSize)
  private var start, end = 0

  override def apply(idx: Int) = {
    if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)
    array(mod(start + idx))
  }

  override def update(idx: Int, elem: A) = {
    if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)
    array(mod(start + idx)) = elem
  }

  override def length = mod(mod(end) - mod(start))

  override def +=(elem: A) = {
    if (size == array.length) resize()
    array(mod(end)) = elem
    end += 1
    this
  }

  override def clear() = {
    start = 0
    end = 0
  }

  override def +=:(elem: A) = {
    if (size == array.length) resize()
    start -= 1
    array(mod(start)) = elem
    this
  }

  override def prependAll(xs: TraversableOnce[A]) =
    xs.toSeq.reverse.foreach(x => x +=: this)

  override def insertAll(idx: Int, elems: Traversable[A]) = {
    if (idx == 0) {
      prependAll(elems)
    } else {
      if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)
      val shift = (idx until size).map(this)
      end = start + idx
      this ++= elems ++= shift
    }
  }

  override def remove(idx: Int) = {
    val ret = this(idx)
    remove(idx, 1)
    ret
  }

  override def remove(idx: Int, count: Int) = {
    if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)
    if (idx + count >= size) {
      end = start + idx
    } else if (count > 0) {
      if (idx == 0) {
        start += count
      } else {
        ((idx + count) until size).foreach(i => this(i - count) = this(i))
        end -= count
      }
    }
  }

  override def iterator = indices.iterator.map(apply)

  override def trimStart(n: Int) = if (n >= size) clear() else if (n >= 0) start += n

  override def trimEnd(n: Int) = if (n >= size) clear() else if (n >= 0) end -= n

  override def head = this(0)

  override def last = this(size - 1)

  private def mod(x: Int) = Math.floorMod(x, array.length)

  private def resize(len: Int = 2 * array.length): Unit = {
    val array2 = Array.ofDim[A](len)
    copyToArray(array2) //TODO: optimize this by doing array.copy(array2, start, len) etc.
    end = size
    start = 0
    array = array2
  }
}
