package com.github.pathikrit.scalgos

/**
 * A data structure that supports interval updates
 * e.g. map(5 -> 60000) = "hello" would set all keys in [5, 60000) to be "hello"
 */
trait IntervalMap[A] {
  import com.github.pathikrit.scalgos.IntervalMap.Interval

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

  override def equals(obj: Any) = obj.isInstanceOf[IntervalMap[A]] && obj.asInstanceOf[IntervalMap[A]].toSeq == toSeq

  override def toString = toSeq map {case (i, v) => s"$i : $v"} mkString ("{", ", ", "}")
}

/**
 * companion object for IntervalMap
 */
object IntervalMap {

  /**
   * @return a new empty IntervalMap
   *         all operations are O(n) except toSeq which is O(n log n) (where n is number of disjoint segments)
   */
  def empty[A]: IntervalMap[A] = new SegmentedIntervalMap[A]

  case class Interval(start: Int, end: Int) {
    require(start <= end)
    def overlaps(r: Interval) = start <= r.start && r.end <= end
    def contains(x: Int) = start <= x && x < end
    override def toString = s"[$start, $end)"
  }

  implicit def toInterval(r: (Int, Int)) = Interval(r._1, r._2)

  private class SegmentedIntervalMap[A] extends IntervalMap[A] {

    private var segments = Map.empty[Interval, A]

    def update(r: Interval, value: A) = {
      clear(r)

      val a = segments flatMap {
        case (k, v) if k.end == r.start && v == value => dropAnd(k, Some(k.start))
        case _ => None
      }

      val b = segments flatMap {
        case (k, v) if r.end == k.start && v == value => dropAnd(k, Some(k.end))
        case _ => None
      }

      val key: Interval = (a.headOption getOrElse r.start, b.headOption getOrElse r.end)
      segments = segments + (key -> value)
    }

    def apply(x: Int) = segments find {_._1 contains x} map {_._2}

    def clear(r: Interval) = {
      segments = segments filterKeys {key => !(r overlaps key)}

      segments find {_._1 contains r.start} map {
        case (k, v) => dropAnd(k, segments = segments + (Interval(k.start, r.start) -> v))
      }

      segments find {_._1 contains r.end} map {
        case (k, v) => dropAnd(k, segments = segments + (Interval(r.end, k.end) -> v))
      }
    }

    private def dropAnd[B](toDrop: Interval, after: => B) = {
      segments = segments - toDrop
      after
    }

    def toSeq = segments.toSeq sortBy {_._1.start}
  }
}
