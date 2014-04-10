package com.github.pathikrit.scalgos

import com.github.pathikrit.scalgos.IntervalMap._

import org.specs2.mutable.Specification

class IntervalMapSpec extends Specification {

  "match brute force implementation" in {
    class DumbIntervalMap[A] extends IntervalMap[A] {
      import collection.mutable

      private val segments = mutable.Map.empty[Int, A]

      implicit def toRange(s: Interval) = s.start until s.end

      def update(r: Interval, value: A) = r foreach {i => segments(i) = value}

      def apply(x: Int) = segments get x

      def clear(r: Interval) = r foreach segments.remove

      def toSeq = {
        segments groupBy {_._2} mapValues {_.keys.toSeq.sorted}
        Nil
      }
    }
    todo
  }

  "work for small case" in {
    val map = IntervalMap.empty[String]
    map(10 -> 90) = "a"
    map(20 -> 30) = "b"
    map(40 -> 50) = "c"
    map(25 -> 45) = "d"
    map(50 -> 100) = "c"
    map.toString mustEqual "{[10, 20) : a, [20, 25) : b, [25, 45) : d, [45, 100) : c}"
  }
}
