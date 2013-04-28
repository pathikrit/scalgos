package com.github.pathikrit.scalgos

import org.specs2.mutable._

import DivideAndConquer._

class DivideAndConquerSpec extends Specification {

  "maxRectangleUnderHistogram" should {

    "work for small inputs" in {
      maxRectangleInHistogram(Nil) must be equalTo 0
      maxRectangleInHistogram(Seq(1)) must be equalTo 1
      maxRectangleInHistogram(Seq(1, 2)) must be equalTo 2
      maxRectangleInHistogram(Seq(2, 1)) must be equalTo 2
      maxRectangleInHistogram(Seq(1, 2, 3)) must be equalTo 4
      maxRectangleInHistogram(Seq(1, 3, 2)) must be equalTo 4
      maxRectangleInHistogram(Seq(3, 1, 2)) must be equalTo 3
      maxRectangleInHistogram(Seq(3, 1, 2)) must be equalTo 3
      maxRectangleInHistogram(Seq(2, 1, 3)) must be equalTo 3
      maxRectangleInHistogram(Seq(2, 3, 1)) must be equalTo 4
    }

    "work for zeroes" in {
      maxRectangleInHistogram(Seq(0)) must be equalTo 0
      maxRectangleInHistogram(Seq(0, 0)) must be equalTo 0
    }

    "fail for negative inputs" in todo

    "match the rewrite algorithm" in {
      val heights = RandomData.positiveSeq()
      maxRectangleInHistogram(heights) must be equalTo MaxRectangleInHistogram(heights map {(_, 1)})
    }
  }

  "binarySearch" should {
    "work on discrete functions" in todo

    def sqrt(d: Double) =  binarySearch[Double, Double](x => x*x, 0, d+1, (x,y) => (x+y)/2, d)

    "work on continuous functions" in {
      def check(x: Double) {
        sqrt(x) must be ~(math.sqrt(x) +/- 1e-9)
      }
      check(34)
      check(2)
      check(1e-19)
      //check(1e29)
      check(0)
      check(1)
      //todo: check for -ve inf, +ve inf and nans?
    }

    "fail to find when missing" in {
      //sqrt(-1) must be empty
      //sqrt(-0.0001) must be empty
    }

    "fail to find when max<=min" in todo
    "fail on boundary conditions" in todo
  }

  "ternarySearch" should {
    "find minima" in todo
    "find maxima" in todo
    "fail if f is not unimodal" in todo
    "fail if right < left" in todo
  }
}
