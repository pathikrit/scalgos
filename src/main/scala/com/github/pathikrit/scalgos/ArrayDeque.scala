package com.github.pathikrit.scalgos

import scala.collection.{generic, mutable}

/**
  * A data structure that provides O(1) get, update, length, append, prepend, clear, trimStart and trimRight
  *
  * @author Pathikrit Bhowmick
  * @tparam A
  */
class ArrayDeque[A] private(var array: Array[AnyRef], var start: Int, var end: Int)
  extends mutable.AbstractBuffer[A]
    with mutable.Buffer[A]
    with generic.GenericTraversableTemplate[A, ArrayDeque]
    with mutable.BufferLike[A, ArrayDeque[A]]
    with mutable.IndexedSeq[A]
    with mutable.IndexedSeqOptimized[A, ArrayDeque[A]]
    with mutable.Builder[A, ArrayDeque[A]]
    with Serializable {

  def this(initialSize: Int = ArrayDeque.defaultInitialSize) = this(ArrayDeque.alloc(initialSize), 0, 0)

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

  override def prependAll(xs: TraversableOnce[A]) = xs.toSeq.reverse.foreach(+=:)

  override def insertAll(idx: Int, elems: Traversable[A]) = {
    checkIndex(idx)
    if (idx == 0) {
      prependAll(elems)
    } else {
      val shift = drop(idx)
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

  override def trimStart(n: Int) = if (n >= size) clear() else if (n > 0) start += n

  override def trimEnd(n: Int) = if (n >= size) clear() else if (n > 0) end -= n

  override def clone() = new ArrayDeque(array.clone, start, end)

  override def slice(from: Int, until: Int) = {
    val left = box(from)
    val right = box(until)
    val len = right - left
    if (len <= 0) {
      ArrayDeque.empty[A]
    } else if (len >= size) {
      clone()
    } else {
      val array2 = ArrayDeque.alloc(len)
      arrayCopy(array2, left, 0, len)
      new ArrayDeque(array2, 0, len)
    }
  }

  override def sliding(window: Int, step: Int) = {
    require(window >= 1 && step >= 1, s"size=$size and step=$step, but both must be positive")
    (indices by step).iterator.map(i => slice(i, i + window))
  }

  override def grouped(n: Int) = sliding(n, n)

  override def copyToArray[B >: A](dest: Array[B], destStart: Int, len: Int) =
    arrayCopy(dest, srcStart = 0, destStart = destStart, maxItems = len)

  /**
    * Trims the capacity of this CircularBuffer's instance to be the current size
    */
  def trimToSize(): Unit = accomodate(size)

  @inline private def mod(x: Int) = x & (array.length - 1)  // modulus using bitmask since array.length is always power of 2

  @inline private def box(i: Int) = if (i <= 0) 0 else if (i >= size) size else i

  private def accomodate(len: Int) = {
    require(len >= size)
    val array2 = ArrayDeque.alloc(len)
    arrayCopy(array2, srcStart = 0, destStart = 0, maxItems = size)
    end = size
    start = 0
    array = array2
  }

  def arrayCopy(dest: Array[_], srcStart: Int, destStart: Int, maxItems: Int) = {
    if(!dest.isDefinedAt(destStart)) throw new IndexOutOfBoundsException(destStart.toString)
    checkIndex(srcStart)
    val toCopy = size min maxItems min (dest.length - destStart)
    if (toCopy > 0) {
      val startIdx = mod(start + srcStart)
      val block1 = toCopy min (array.length - startIdx)
      Array.copy(src = array, srcPos = startIdx, dest = dest, destPos = destStart, length = block1)
      if (block1 < toCopy) {
        Array.copy(src = array, srcPos = 0, dest = dest, destPos = block1, length = toCopy - block1)
      }
    }
    dest
  }

  private def checkIndex(idx: Int) = if(!isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)

  private def ensureCapacity() = if (size == array.length - 1) accomodate(array.length)

  override def companion = ArrayDeque

  override def result(): ArrayDeque[A] = this
}

object ArrayDeque extends generic.SeqFactory[ArrayDeque] {
  implicit def canBuildFrom[A]: generic.CanBuildFrom[Coll, A, ArrayDeque[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

  override def newBuilder[A]: mutable.Builder[A, ArrayDeque[A]] = new ArrayDeque[A]()

  val defaultInitialSize = 8

  private[ArrayDeque$] def alloc(len: Int) = {
    var i = len max defaultInitialSize
    //See: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    i |= i >> 1; i |= i >> 2; i |= i >> 4; i |= i >> 8; i |= i >> 16
    Array.ofDim[AnyRef](i + 1)
  }
}