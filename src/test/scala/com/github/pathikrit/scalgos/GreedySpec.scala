package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import scala.collection.mutable

import Greedy._

class GreedySpec extends Specification {
  "maxRectangleInHistogram" should {
    "work for small inputs" in todo

    "work for zeroes" in todo

    "fail for negative or zero widths inputs" in todo

    "match the naive implementation" in {
      def maxRectangleInHistogram2(heights: Seq[Int]) = {
        val stack = new mutable.Stack[(Int, Int)]
        var ans = 0
        for ((h, i) <- (heights :+ 0).zipWithIndex) {
          var pos = i
          while(stack.headOption.exists(_._1 > h)) {
            val (y, x) = stack.pop()
            ans = ans max (y * (i - x))
            pos = x
          }
          stack push (h->pos)
        }
        ans
      }
      val blocks = RandomData.list(min = 0, max = 100)
      maxRectangleInHistogram(blocks) must be equalTo maxRectangleInHistogram2(blocks)
    }
  }
}
