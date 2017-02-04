package com.github.pathikrit.scalgos

import scala.collection.mutable

/**
  * A data structure that provides O(1) get, update, length, append, prepend, clear, trimStart and trimRight
  * @tparam A
  */
class CircularBuffer[A] private(var array: Array[AnyRef], var start: Int, var end: Int) extends mutable.Buffer[A] {
  override def apply(idx: Int) = {
    checkIndex(idx)
    array(mod(start + idx)).asInstanceOf[A]
  }

  override def update(idx: Int, elem: A) = {
    checkIndex(idx)
    array(mod(start + idx)) = elem.asInstanceOf[AnyRef]
  }

  override def length = mod(end - start)

  override def +=(elem: A) = {
    ensureCapacity()
    array(end) = elem.asInstanceOf[AnyRef]
    end = mod(end + 1)
    this
  }

  override def clear() = start = end

  override def +=:(elem: A) = {
    ensureCapacity()
    start = mod(start - 1)
    array(start) = elem.asInstanceOf[AnyRef]
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
        ((idx + count) until size).foreach(i => this(i - count) = this(i)) //TODO: use arrayCopy here
        end = mod(end - count)
      }
    }
  }

  override def iterator = indices.iterator.map(apply)

  override def trimStart(n: Int) = if (n >= size) clear() else if (n >= 0) start += n

  override def trimEnd(n: Int) = if (n >= size) clear() else if (n >= 0) end -= n

  override def head = this(0)

  override def last = this(size - 1)

  override def init = drop(1)

  override def tail = dropRight(1)

  override def drop(n: Int) = slice(n, size)

  override def dropRight(n: Int) = slice(0, size - n)

  override def take(n: Int) = slice(0, n)

  override def takeRight(n: Int) = slice(size - n, n)

  override def clone() = new CircularBuffer(array.clone, start, end)

  override def slice(from: Int, until: Int) = {
    var left = box(from)
    var right = box(until)
    val len = right - left
    if (len <= 0) {
      CircularBuffer.empty[A]
    } else if (len >= size) {
      clone()
    } else {
      val array2 = CircularBuffer.alloc(len)
      left = mod(start + left)
      right = mod(start + right)
      if (left <= right) {
        Array.copy(src = array, srcPos = left, dest = array2, destPos = 0, length = len)
      } else {
        Array.copy(src = array, srcPos = left, dest = array2, destPos = 0, length = size - left)
        Array.copy(src = array, srcPos = 0, dest = array2, destPos = size - left, length = right)
      }
      new CircularBuffer(array2, 0, len)
    }
  }

  override def sliding(window: Int, step: Int) = {
    require(window >= 1 && step >= 1, s"size=$size and step=$step, but both must be positive")
    (indices by step).iterator.map(i => slice(i, i + window))
  }

  override def grouped(n: Int) = sliding(n, n)

  override def copyToArray[B >: A](dest: Array[B], destStart: Int, len: Int) = {
    if(!dest.isDefinedAt(destStart)) throw new IndexOutOfBoundsException(destStart.toString)
    if (len > 0) arrayCopy(dest.asInstanceOf[Array[AnyRef]], destStart, len)
  }

  /**
    * Trims the capacity of this CircularBuffer's instance to be the current size
    */
  def trimToSize(): Unit = accomodate(size)

  @inline private def mod(x: Int) = x & (array.length - 1)  // modulus using bitmask since array.length is always power of 2

  @inline private def box(i: Int) = if (i <= 0) 0 else if (i >= size) size else i

  private def accomodate(len: Int) = {
    require(len >= size)
    val array2 = CircularBuffer.alloc(len)
    arrayCopy(array2, 0, size)
    end = size
    start = 0
    array = array2
  }

  private def arrayCopy(dest: Array[AnyRef], destStart: Int, len: Int) = {
    val toCopy = size min len min (dest.length - destStart)
    val block1 = toCopy min (array.length - start)
    Array.copy(src = array, srcPos = start, dest = dest, destPos = destStart, length = block1)
    if (block1 < toCopy) {
      Array.copy(src = array, srcPos = mod(start + block1), dest = dest, destPos = block1, length = toCopy - block1)
    }
  }

  private def checkIndex(idx: Int) = if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)

  private def ensureCapacity() = if (size == array.length - 1) accomodate(array.length)
}

object CircularBuffer {
  private[CircularBuffer] val minimumCapacity = 8

  def apply[A](initialSize: Int = minimumCapacity) = new CircularBuffer[A](alloc(initialSize), 0, 0)

  def empty[A] = CircularBuffer[A](0)

  private[CircularBuffer] def alloc(len: Int) = {
    var i = len max minimumCapacity
    //See: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    i |= i >> 1; i |= i >> 2; i |= i >> 4; i |= i >> 8; i |= i >> 16
    Array.ofDim[AnyRef](i + 1)
  }
}