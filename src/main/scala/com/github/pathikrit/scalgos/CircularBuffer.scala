package com.github.pathikrit.scalgos

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * A data structure that provides O(1) get, update, length, append, prepend, clear, trimStart and trimRight
  * @tparam A
  */
class CircularBuffer[A: ClassTag](initialSize: Int = 1<<4) extends mutable.Buffer[A] {
  private var array = alloc(initialSize)
  private var start, end = 0

  override def apply(idx: Int) = {
    checkIndex(idx)
    array(mod(start + idx))
  }

  override def update(idx: Int, elem: A) = {
    checkIndex(idx)
    array(mod(start + idx)) = elem
  }

  override def length = mod(end - start)

  override def +=(elem: A) = {
    ensureCapacity()
    array(end) = elem
    end = mod(end + 1)
    this
  }

  override def clear() = start = end

  override def +=:(elem: A) = {
    ensureCapacity()
    start = mod(start - 1)
    array(start) = elem
    this
  }

  override def prependAll(xs: TraversableOnce[A]) =
    xs.toSeq.reverse.foreach(x => x +=: this)

  override def insertAll(idx: Int, elems: Traversable[A]) = {
    checkIndex(idx)
    if (idx == 0) {
      prependAll(elems)
    } else {
      val shift = (idx until size).map(this)
      end = mod(start + idx)
      this ++= elems ++= shift
    }
  }

  override def remove(idx: Int) = {
    val elem = this(idx)
    remove(idx, 1)
    elem
  }

  override def remove(idx: Int, count: Int) = {
    checkIndex(idx)
    if (idx + count >= size) {
      end = mod(start + idx)
    } else if (count > 0) {
      if (idx == 0) {
        start = mod(start + count)
      } else {
        ((idx + count) until size).foreach(i => this(i - count) = this(i))
        end = mod(end - count)
      }
    }
  }

  /**
    * Trims the capacity of this CircularBuffer's instance to be the current size
    */
  def trimToSize(): Unit = accomodate(size)

  override def iterator = indices.iterator.map(apply)

  override def trimStart(n: Int) = if (n >= size) clear() else if (n >= 0) start += n

  override def trimEnd(n: Int) = if (n >= size) clear() else if (n >= 0) end -= n

  override def head = this(0)

  override def last = this(size - 1)

  override def copyToArray[B >: A](dest: Array[B], destStart: Int, len: Int) = {
    if(!dest.isDefinedAt(destStart)) throw new IndexOutOfBoundsException(destStart.toString)
    if (len > 0) {
      val toCopy = size min len min (dest.length - destStart)
      val block1 = toCopy min (array.length - start)
      Array.copy(src = array, srcPos = start, dest = dest, destPos = destStart, length = block1)
      if (block1 < toCopy) {
        Array.copy(src = array, srcPos = mod(start + block1), dest = dest, destPos = block1, length = toCopy - block1)
      }
    }
  }

  @inline private def mod(x: Int) = x & (array.length - 1)  // modulus using bitmask since array.length is always power of 2

  private def accomodate(len: Int) = {
    require(len >= size)
    val array2 = alloc(len)
    copyToArray(array2)
    end = size
    start = 0
    array = array2
  }

  private def alloc(len: Int) = {
    var i = len     //See: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    i |= i >> 1; i |= i >> 2; i |= i >> 4; i |= i >> 8; i |= i >> 16
    Array.ofDim[A]((i + 1) max (1<<4))
  }

  private def checkIndex(idx: Int) = if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)

  private def ensureCapacity() = if (size == array.length - 1) accomodate(array.length)
}
