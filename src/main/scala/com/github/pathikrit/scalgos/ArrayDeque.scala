package com.github.pathikrit.scalgos

import scala.collection.{GenSeq, generic, mutable}
import scala.reflect.ClassTag

/** An implementation of a double-ended queue that internally uses a resizable circular buffer
  *  Append, prepend, removeFirst, removeLast and random-access (indexed-lookup and indexed-replacement)
  *  take amortized constant time. In general, removals and insertions at i-th index are O(min(i, n-i))
  *  and thus insertions and removals from end/beginning are fast.
  *
  *  @author  Pathikrit Bhowmick
  *  @version 2.12
  *  @since   2.12
  *
  *  @tparam A  the type of this ArrayDeque's elements.
  *
  *  @define Coll `mutable.ArrayDeque`
  *  @define coll array deque
  *  @define thatinfo the class of the returned collection. In the standard library configuration,
  *    `That` is always `ArrayDeque[B]` because an implicit of type `CanBuildFrom[ArrayDeque, B, ArrayDeque[B]]`
  *    is defined in object `ArrayDeque`.
  *  @define bfinfo an implicit value of class `CanBuildFrom` which determines the
  *    result class `That` from the current representation type `Repr`
  *    and the new element type `B`. This is usually the `canBuildFrom` value
  *    defined in object `ArrayDeque`.
  *  @define orderDependent
  *  @define orderDependentFold
  *  @define mayNotTerminateInf
  *  @define willNotTerminateInf
  */
@SerialVersionUID(1L)
class ArrayDeque[A] private(var array: Array[AnyRef], var start: Int, var end: Int)
  extends mutable.AbstractBuffer[A]
    with mutable.Buffer[A]
    with generic.GenericTraversableTemplate[A, ArrayDeque]
    with mutable.BufferLike[A, ArrayDeque[A]]
    with mutable.IndexedSeq[A]
    with mutable.IndexedSeqOptimized[A, ArrayDeque[A]]
    with mutable.Builder[A, ArrayDeque[A]]
    with Serializable {

  private[this] var mask = 0   // modulus using bitmask since array.length is always power of 2
  set(array, start, end)

  def this(initialSize: Int = ArrayDeque.defaultInitialSize) = this(ArrayDeque.alloc(initialSize), 0, 0)

  override def apply(idx: Int) = {
    checkIndex(idx)
    get(idx).asInstanceOf[A]
  }

  override def update(idx: Int, elem: A) = {
    checkIndex(idx)
    set(idx, elem.asInstanceOf[AnyRef])
  }

  override def +=(elem: A) = {
    ensureCapacity()
    array(end) = elem.asInstanceOf[AnyRef]
    end = (end + 1) & mask
    this
  }

  override def +=:(elem: A) = {
    ensureCapacity()
    start = (start - 1) & mask
    array(start) = elem.asInstanceOf[AnyRef]
    this
  }

  override def ++=:(xs: TraversableOnce[A]) =
    xs.foldRight(this)((x, coll) => x +=: coll).asInstanceOf[this.type]

  override def insertAll(idx: Int, elems: scala.collection.Traversable[A]) = {
    checkIndex(idx)
    val src = elems.toBuffer
    /*val finalLength = src.length + this.length
    // Either we resize right away or move prefix right or suffix left
    if (2*finalLength >= array.length) {
      val array2 = ArrayDeque.alloc(finalLength)
      arrayCopy(dest = array2, srcStart = 0, destStart = 0, maxItems = idx)
      Array.copy(src = src, srcPos = 0, dest = array2, destPos = idx, length = src.length)
      arrayCopy(dest = array2, srcStart = idx, destStart = idx + src.length, maxItems = size)
      set(array = array2, start = 0, end = finalLength)
    } else {*/
      // TODO: choose to move prefix right or suffix left
      val suffix = drop(idx)
      end = (start + idx) & mask
      this ++= src ++= suffix
    /*}*/
  }

  override def remove(idx: Int, count: Int): Unit = {
    checkIndex(idx)
    if (count <= 0) return
    val removals = (size - idx) min count
    // If we are removing more than half the elements, its cheaper to start over
    // Else, either move the prefix right or the suffix left - whichever is shorter
    if(2*removals >= size) {
      val array2 = ArrayDeque.alloc(size - removals)
      arrayCopy(dest = array2, srcStart = 0, destStart = 0, maxItems = idx)
      arrayCopy(dest = array2, srcStart = idx + removals - 1, destStart = idx, maxItems = size)
      set(array = array2, start = 0, end = size - removals)
    } else if (size - idx <= idx + removals) {
      (idx until size) foreach {i =>
        val elem = if (i + removals < size) get(i + removals) else null
        set(i, elem)
      }
      end = (end - removals) & mask
    } else {
      (0 until (idx + removals)).reverse foreach {i =>
        val elem = if (i - removals < 0) null else get(i - removals)
        set(i, elem)
      }
      start = (start + removals) & mask
    }
  }

  override def remove(idx: Int) = {
    val elem = this(idx)
    remove(idx, 1)
    elem
  }

  def removeFirst(): Option[A] = {
    if (isEmpty) {
      None
    } else {
      val elem = array(start)
      array(start) = null
      start = (start + 1) & mask
      Some(elem.asInstanceOf[A])
    }
  }

  def removeLast(): Option[A] = {
    if (isEmpty) {
      None
    } else {
      end = (end - 1) & mask
      val elem = array(end)
      array(end) = null
      Some(elem.asInstanceOf[A])
    }
  }

  override def length = (end - start) & mask

  override def isEmpty = start == end

  override def clone() = new ArrayDeque(array.clone, start, end)

  override def clear() = if (nonEmpty) set(array = ArrayDeque.alloc(ArrayDeque.defaultInitialSize), start = 0, end = 0)

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
      arrayCopy(dest = array2, srcStart = left, destStart = 0, maxItems = len)
      new ArrayDeque(array2, 0, len)
    }
  }

  override def sliding(window: Int, step: Int) = {
    require(window >= 1 && step >= 1, s"size=$size and step=$step, but both must be positive")
    (indices by step).iterator.map(i => slice(i, i + window))
  }

  override def grouped(n: Int) = sliding(n, n)

  override def copyToArray[B >: A](dest: Array[B], destStart: Int, len: Int) =
    arrayCopy(dest = dest, srcStart = 0, destStart = destStart, maxItems = len)

  override def companion = ArrayDeque

  override def result() = this

  override def stringPrefix = "ArrayDeque"

  override def toArray[B >: A: ClassTag] = {
    val array2 = Array.ofDim[B](size)
    arrayCopy(dest = array2, srcStart = 0, destStart = 0, maxItems = size)
    array2
  }

  def arrayCopy(dest: Array[_], srcStart: Int, destStart: Int, maxItems: Int): Unit = {
    checkIndex(destStart, dest)
    checkIndex(srcStart)
    val toCopy = maxItems min (size - srcStart) min (dest.length - destStart)
    if (toCopy > 0) {
      val startIdx = (start + srcStart) & mask
      val block1 = toCopy min (array.length - startIdx)
      Array.copy(src = array, srcPos = startIdx, dest = dest, destPos = destStart, length = block1)
      if (block1 < toCopy) {
        Array.copy(src = array, srcPos = 0, dest = dest, destPos = block1, length = toCopy - block1)
      }
    }
  }

  /**
    * Trims the capacity of this CircularBuffer's instance to be the current size
    */
  def trimToSize(): Unit = resize(size - 1)

  @inline private def checkIndex(idx: Int, seq: GenSeq[_] = this) = if(!seq.isDefinedAt(idx)) throw new IndexOutOfBoundsException(idx.toString)

  @inline private def box(i: Int) = if (i <= 0) 0 else if (i >= size) size else i

  @inline private def get(idx: Int) = array((start + idx) & mask)

  @inline private def set(idx: Int, elem: AnyRef) = array((start + idx) & mask) = elem

  @inline private def set(array: Array[AnyRef], start: Int, end: Int) = {
    this.array = array
    this.mask = array.length - 1
    assert((array.length & mask) == 0, s"Array.length must be power of 2")
    this.start = start
    this.end = end
  }

  private def ensureCapacity() = if (size == array.length - 1) resize(array.length)

  private def resize(len: Int) = {
    val array2 = ArrayDeque.alloc(len)
    arrayCopy(dest = array2, srcStart = 0, destStart = 0, maxItems = size)
    set(array = array2, start = 0, end = size)
  }
}

object ArrayDeque extends generic.SeqFactory[ArrayDeque] {
  implicit def canBuildFrom[A]: generic.CanBuildFrom[Coll, A, ArrayDeque[A]] =
    ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

  override def newBuilder[A]: mutable.Builder[A, ArrayDeque[A]] = new ArrayDeque[A]()

  val defaultInitialSize = 8

  /**
    * Allocates an array whose size is next power of 2 > $len
    *
    * @param len
    * @return
    */
  private[ArrayDeque] def alloc(len: Int) = {
    var i = len max defaultInitialSize
    //See: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    i |= i >> 1; i |= i >> 2; i |= i >> 4; i |= i >> 8; i |= i >> 16
    Array.ofDim[AnyRef](i + 1)
  }
}