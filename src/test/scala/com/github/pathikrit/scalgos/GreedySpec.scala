package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Greedy._

class GreedySpec extends Specification {
  "maxRectangleInHistogram" should {
    "work for small inputs" in todo

    "work for zeroes" in todo

    "fail for negative or zero widths inputs" in todo

    "match the divide and conquer algorithm" in {
      val blocks = RandomData.list(min = 0, max = 100)
      val expectedArea = DivideAndConquer.maxRectangleInHistogram(blocks)
      maxRectangleInHistogram(blocks) aka blocks.toString() must be equalTo expectedArea
    }
  }
}
