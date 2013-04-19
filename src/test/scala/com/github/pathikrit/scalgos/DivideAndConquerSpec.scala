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

    "work on continuous functions" in {
      // todo: run this on random rumbers
      val goal = 34
      val binarySearchSqrt = binarySearch[Double, Double](x => x*x, 0, goal+1, (x,y) => (x+y)/2, goal)
      binarySearchSqrt.get must be equalTo math.sqrt(goal)
    }

    "fail to find when missing" in todo
    "fail to find when max<=min" in todo
    "fail on boundary conditions" in todo
  }
}
