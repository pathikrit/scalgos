package com.github.pathikrit.scalgos

import IntervalMap._

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

      def toSeq = throw new UnsupportedOperationException

      override def toString = segments.toSeq sortBy {_._1} map {case (i, v) => s"$i: $v"} mkString ", "
    }

    val map1 = IntervalMap.empty[Int]
    val map2 = new DumbIntervalMap[Int]
    val (n, r, p, v) = (10000, 100, 0.8, 20)

    examplesBlock {
      import RandomData._
      for (i <- 1 to n) {
        val start = integer(-n, n)
        val end = integer(start, n+1)

        if(number() < p) {
          val value = integer(v)
          map1(start -> end) = value
          map2(start -> end) = value
        } else {
          map1 clear (start -> end)
          map2 clear (start -> end)
        }

        (-2*r to 2*r) forall {i => map1(i) == map2(i)} must beTrue
      }
    }
  }
}
