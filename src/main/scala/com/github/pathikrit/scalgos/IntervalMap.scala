package com.github.pathikrit.scalgos

import scala.collection.mutable

/**
 * A data structure that supports interval updates
 * e.g. map(5 -> 60000) = "hello" would set all keys in [5, 60000) to be "hello"
 */
trait IntervalMap[A] {
  import IntervalMap.Interval

  /**
   * Set all values in range to value
   *
   * @param r range
   * @param value value to set to
   */
  def update(r: Interval, value: A)

  /**
   * Value at point x
   *
   * @return Some(y) if a value exists else None
   */
  def apply(x: Int): Option[A]

  /**
   * Clear all mappings in given range
   *
   * @param r range
   */
  def clear(r: Interval)

  /**
   * Extract to a seq
   * intervals are guaranteed to be disjoint and from left to right
   * adjacent intervals are guaranteed to have different values
   * Use this method for all scala collection goodies
   *
   * @return sequence of disjoint intervals that have values
   */
  def toSeq: Seq[(Interval, A)]

  override def hashCode = toSeq.hashCode()

  override def equals(obj: Any) = obj match {
    case that: IntervalMap[A] => that.toSeq == this.toSeq
    case _ => false
  }

  override def toString = toSeq map {case (i, v) => s"$i : $v"} mkString ("{", ", ", "}")
}

/**
 * companion object for IntervalMap
 */
object IntervalMap {

  /**
   * @return a new empty IntervalMap
   *         all operations are O(n) except toSeq which is O(n log n) (where n is number of disjoint segments)
   *         TODO: Make these O(log n)
   */
  def empty[A]: IntervalMap[A] = new SegmentedIntervalMap[A]

  /**
   * Models a half-closed interval [start, end)
   */
  case class Interval(start: Int, end: Int) {
    require(start <= end)
    def overlaps(r: Interval) = start <= r.start && r.end <= end
    def contains(x: Int) = start <= x && x < end
    override def toString = s"[$start, $end)"
  }

  implicit val toInterval = Interval.tupled

  private[this] class SegmentedIntervalMap[A] extends IntervalMap[A] {

    private[this] val segments = mutable.Map.empty[Interval, A]

    override def update(r: Interval, value: A) = {
      clear(r)
      val a = segments collect { case (k @ Interval(_, r.start), `value`) =>
        unset(k)
        k.start
      }
      val b = segments collect { case (k @ Interval(r.end, _), `value`) =>
        unset(k)
        k.end
      }
      set((a.headOption getOrElse r.start) -> (b.headOption getOrElse r.end), value)
    }

    override def apply(x: Int) = segments find {_._1 contains x} map {_._2}

    override def clear(r: Interval) = {
      segments.keys filter r.overlaps foreach unset

      segments foreach {case (k, v)  =>
        if (k contains r.start) {
          unset(k)
          set(k.start -> r.start, v)
        }
        if (k contains r.end) {
          unset(k)
          set(r.end -> k.end, v)
        }
      }
    }

    override def toSeq = segments.toSeq.sortBy(_._1.start)

    private[this] def unset(i: Interval) = segments -= i
    private[this] def set(i: Interval, value: A) = segments(i) = value
  }
}
