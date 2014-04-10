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
        //val valMap = segments groupBy {_._2} mapValues {_.keySet} mapValues meld
        Nil
      }

      def meld(s: Set[Int]) = {
        var intervals = Seq.empty[Interval]

        val max = s.max
        for (start <- s.min to max if intervals.isEmpty || start >= intervals.last.end) {
          var end = 0
          for(i <- start to max if s contains i) {
            end = i
          }
          intervals = intervals :+ Interval(start, end)
        }
        intervals
      }
    }
    import RandomData._

    val map1 = IntervalMap.empty[Int]
    val map2 = new DumbIntervalMap[Int]
    map1 must be equalTo map2

    examplesBlock {
      for (i <- 1 to 1000) {
        val start = integer()
        val end = integer(start = start)
        if(number() < 0.8) {
          val value = integer(20)
          map1(start -> end) = value
          map2(start -> end) = value
        } else {
          map1 clear (start -> end)
          map2 clear (start -> end)
        }
        map1 must be equalTo map2
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
